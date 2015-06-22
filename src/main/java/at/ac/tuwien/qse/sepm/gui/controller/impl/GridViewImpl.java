package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.controller.GridView;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.*;
import at.ac.tuwien.qse.sepm.gui.grid.PaginatedImageGrid;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GridViewImpl implements GridView {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private PhotoService photoService;
    @Autowired
    private ImportService importService;
    @Autowired
    private FlickrService flickrService;
    @Autowired
    private DropboxService dropboxService;
    @Autowired
    private PhotographerService photographerService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private Organizer organizer;
    @Autowired
    private Inspector<Photo> inspector;
    @Autowired
    private Menu menu;
    @Autowired
    private ImageCache imageCache;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private ExifService exifService;

    @FXML
    private BorderPane root;

    private PaginatedImageGrid grid;
    private boolean disableReload = false;
    private boolean treeViewActive = false;

    @Autowired
    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;

        if (grid == null) {
            this.grid = new PaginatedImageGrid(menu);
        }
    }

    @FXML
    private void initialize() {
        LOGGER.debug("initializing");

        this.grid = new PaginatedImageGrid(menu);
        root.setCenter(grid);
        menu.addListener(new MenuListener());
        organizer.setFilterChangeAction(this::handleFilterChange);
        root.setCenter(grid);

        // Selected photos are shown in the inspector.
        grid.setSelectionChangeAction(inspector::setEntities);

        // CTRL+A select all photos.
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.A) {
                grid.selectAll();
            }
        });

        // Updated photos that no longer match the filter are removed from the grid.
        inspector.setUpdateHandler(() -> {
            inspector.getEntities().stream()
                    .filter(organizer.getFilter().negate())
                    .forEach(grid::removePhoto);
            inspector.getEntities().stream()
                    .filter(organizer.getFilter())
                    .forEach(grid::updatePhoto);
        });

        // Apply the initial filter.
        handleFilterChange();

        // subscribe to photo events
        photoService.subscribeCreate(this::handlePhotoCreated);
        photoService.subscribeUpdate(this::handlePhotoUpdated);
        photoService.subscribeDelete(this::handlePhotoDeleted);
    }

    private void handleImportError(Throwable error) {
        LOGGER.error("import error", error);

        // queue an update in the main gui
        Platform.runLater(() -> ErrorDialog.show(root, "Import fehlgeschlagen", "Fehlermeldung: " + error.getMessage()));
    }

    private void handlePhotoCreated(Photo photo) {
        Platform.runLater(() -> {
            if (organizer.getFilter().test(photo)) {
                grid.addPhoto(photo);
            }
        });
    }

    private void handlePhotoUpdated(Photo photo) {
        Platform.runLater(() -> {
            // TODO: should we update filter
            grid.updatePhoto(photo);
        });
    }

    private void handlePhotoDeleted(Path file) {
        Platform.runLater(() -> {
            // TODO: should we update filter

            // lookup photo by path
            Optional<Photo> photo = grid.getPhotos().stream()
                    .filter(p -> p.getFile().equals(file))
                    .findFirst();

            if (photo.isPresent())
                grid.removePhoto(photo.get());
        });
    }


    /**
     * Called whenever a new photo is imported
     *
     * @param photo The newly imported    photo
     */
    private void handleImportedPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {
            disableReload = true;

            // Ignore photos that are not part of the current filter.
            if (!organizer.getFilter().test(photo)) {
                disableReload = false;
                return;
            }
            grid.addPhoto(photo);

            disableReload = false;
        });
    }

    private void handleFilterChange() {
        if (!disableReload)
            reloadImages();
    }

    private void reloadImages() {
        try {

            grid.setPhotos(photoService.getAllPhotos(organizer.getFilter()).stream()
                            .sorted((p1, p2) -> p2.getData().getDatetime().compareTo(p1.getData().getDatetime()))
                            .collect(Collectors.toList()));
        } catch (ServiceException ex) {
            LOGGER.error("failed loading fotos", ex);
            ErrorDialog.show(root, "Laden von Fotos fehlgeschlagen",
                    "Fehlermeldung: " + ex.getMessage());
        }
    }

    private class MenuListener implements Menu.Listener {

        @Override public void onPresent(Menu sender) {
            FullscreenWindow fullscreen = new FullscreenWindow(imageCache);
            List<Photo> getSelectedPhoto = new ArrayList<>();
            getSelectedPhoto.addAll(grid.getSelected());
            fullscreen.present(grid.getPhotos(), getSelectedPhoto.get(0));
        }

        @Override public void onFlickr(Menu sender) {
            FlickrDialog flickrDialog = new FlickrDialog(root, "Flickr Import", flickrService, exifService, ioHandler);
            Optional<List<Photo>> photos = flickrDialog.showForResult();
            if (!photos.isPresent()) return;
        }

        @Override public void onJourney(Menu sender) {
            JourneyDialog dialog = new JourneyDialog(root, clusterService);
            dialog.showForResult();
        }

        @Override public void onDelete(Menu sender) {
            Collection<Photo> selection = grid.getSelected();
            if (selection.isEmpty()) {
                return;
            }

            DeleteDialog deleteDialog = new DeleteDialog(root, selection.size());
            Optional<Boolean> confirmed = deleteDialog.showForResult();
            if (!confirmed.isPresent() || !confirmed.get()) return;

            try {
                photoService.deletePhotos(selection);
            } catch (ServiceException ex) {
                LOGGER.error("failed deleting photos", ex);
                ErrorDialog.show(root, "Fehler beim Löschen", "Die ausgewählten Fotos konnten nicht gelöscht werden.");
            }

            selection.forEach(grid::removePhoto);
        }

        @Override public void onExport(Menu sender) {
            Collection<Photo> selection = grid.getSelected();

            ExportDialog dialog = new ExportDialog(root, dropboxService, selection.size());

            Optional<String> destinationPath = dialog.showForResult();
            if (!destinationPath.isPresent()) return;

            dropboxService.uploadPhotos(selection, destinationPath.get(), photo -> {
                // TODO: progressbar
            }, exception -> ErrorDialog.show(root, "Fehler beim Export",
                    "Fehlermeldung: " + exception.getMessage()));
        }
    }
}
