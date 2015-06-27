package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.controller.GridView;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.*;
import at.ac.tuwien.qse.sepm.gui.grid.PaginatedImageGrid;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
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
    private IOHandler ioHandler;
    @Autowired
    private ExifService exifService;

    @Autowired
    private WorkspaceService workspaceService;

    @FXML
    private StackPane root;

    @FXML
    private BorderPane content;

    @FXML
    private Node folderDropTarget;

    private PaginatedImageGrid grid;
    private boolean disableReload = false;
    private boolean treeViewActive = false;

    @FXML
    private void initialize() {
        LOGGER.debug("initializing");

        this.grid = new PaginatedImageGrid(menu);
        content.setCenter(grid);
        menu.addListener(new MenuListener());
        organizer.setFilterChangeAction(this::handleFilterChange);
        content.setCenter(grid);

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
            Collection<Photo> photos = inspector.getEntities();

            photos.stream()
                    .filter(organizer.getUsedFilter().negate())
                    .forEach(grid::removePhoto);
            photos.stream()
                    .filter(organizer.getUsedFilter())
                    .forEach(grid::updatePhoto);
        });

        // Apply the initial filter.
        handleFilterChange();

        // subscribe to photo events
        photoService.subscribeCreate(this::handlePhotoCreated);
        photoService.subscribeUpdate(this::handlePhotoUpdated);
        photoService.subscribeDelete(this::handlePhotoDeleted);

        root.setOnDragEntered(this::handleDragEntered);
        root.setOnDragOver(this::handleDragOver);
        root.setOnDragDropped(this::handleDragDropped);
        root.setOnDragExited(this::handleDragExited);

        folderDropTarget.setVisible(false);

        try {
            if (workspaceService.getDirectories().isEmpty()) {
                folderDropTarget.setVisible(true);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden der Bildordner", "");
        }
    }

    private void handleImportError(Throwable error) {
        LOGGER.error("import error", error);

        // queue an update in the main gui
        Platform.runLater(() -> ErrorDialog.show(root, "Import fehlgeschlagen", "Fehlermeldung: " + error.getMessage()));
    }

    private void handlePhotoCreated(Photo photo) {
        Platform.runLater(() -> {
            if (organizer.getUsedFilter().test(photo)) {
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


    private void handleFilterChange() {
        if (!disableReload)
            reloadImages();
    }

    private void reloadImages() {
        try {

            grid.setPhotos(photoService.getAllPhotos(organizer.getUsedFilter()).stream()
                            .sorted((p1, p2) -> p2.getData().getDatetime().compareTo(p1.getData().getDatetime()))
                            .collect(Collectors.toList()));
        } catch (ServiceException ex) {
            LOGGER.error("failed loading fotos", ex);
            ErrorDialog.show(root, "Laden von Fotos fehlgeschlagen",
                    "Fehlermeldung: " + ex.getMessage());
        }
    }

    private void handleDragEntered(DragEvent event) {
        LOGGER.debug("drag entered");
        folderDropTarget.setVisible(true);
        event.consume();
    }

    private void handleDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.LINK);
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        LOGGER.debug("drag dropped");

        Dragboard dragboard = event.getDragboard();
        boolean success = dragboard.hasFiles();
        if (success) {
            dragboard.getFiles().forEach(f -> LOGGER.debug("dropped file {}", f));
            for (File file : dragboard.getFiles()) {
                try {
                    workspaceService.addDirectory(file.toPath());
                } catch (ServiceException ex) {
                    LOGGER.error("Couldn't add directory {}");
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        LOGGER.debug("drag exited");
        folderDropTarget.setVisible(false);
        event.consume();
    }

    private class MenuListener implements Menu.Listener {

        @Override public void onPresent(Menu sender) {
            FullscreenWindow fullscreen = new FullscreenWindow(photoService);

            Set<Photo> selected = grid.getSelected();

            Photo start = grid.getActivePhoto();
            if (selected.size() == 1) {
                start = selected.iterator().next();
            }
            
            fullscreen.present(grid.getPhotos(), start);
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
