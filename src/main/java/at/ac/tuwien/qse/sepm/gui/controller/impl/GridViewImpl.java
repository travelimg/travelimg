package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.controller.GridView;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.FlickrDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.JourneyDialog;
import at.ac.tuwien.qse.sepm.gui.grid.PaginatedImageGrid;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
    private PhotographerService photographerService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private Organizer organizer;
    @Autowired
    private Inspector inspector;
    @Autowired
    private ImageCache imageCache;

    @FXML
    private BorderPane root;
    @FXML
    private ScrollPane gridContainer;

    private PaginatedImageGrid grid;
    private boolean disableReload = false;

    @FXML
    private void initialize() {
        LOGGER.debug("initializing");

        this.grid = new PaginatedImageGrid(imageCache);
        gridContainer.setContent(grid);

        organizer.setImportAction(() -> {
            ImportDialog dialog = new ImportDialog(root, photographerService);
            Optional<List<Photo>> photos = dialog.showForResult();
            if (!photos.isPresent())
                return;
            importService
                    .importPhotos(photos.get(), this::handleImportedPhoto, this::handleImportError);
        });
        organizer.setFlickrAction(() -> {
            FlickrDialog flickrDialog = new FlickrDialog(root, "Flickr Import", flickrService);
            Optional<List<Photo>> photos = flickrDialog.showForResult();
            if (!photos.isPresent()) return;
            importService.importPhotos(photos.get(), this::handleImportedPhoto, this::handleImportError);
        });

        organizer.setPresentAction(() -> {
            FullscreenWindow fullscreen = new FullscreenWindow(imageCache);
            fullscreen.present(grid.getPhotos(), grid.getActivePhoto());
        });
        organizer.setJourneyAction(() -> {
            JourneyDialog dialog = new JourneyDialog(root, clusterService);
            dialog.showForResult();
        });

        organizer.setFilterChangeAction(this::handleFilterChange);

        // Selected photos are shown in the inspector.
        grid.setSelectionChangeAction(inspector::setActivePhotos);

        // CTRL+A select all photos.
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.A) {
                grid.selectAll();
            }
        });

        // Updated photos that no longer match the filter are removed from the grid.
        inspector.setUpdateHandler(() -> {
            inspector.getActivePhotos().stream()
                    .filter(organizer.getFilter().negate())
                    .forEach(grid::removePhoto);
            inspector.getActivePhotos().stream()
                    .filter(organizer.getFilter())
                    .forEach(grid::updatePhoto);
        });

        // Deleted photos are removed from the grid.
        inspector.setDeleteHandler(() -> {
            inspector.  getActivePhotos().forEach(grid::removePhoto);
        });

        // Apply the initial filter.
        handleFilterChange();
    }

    private void handleImportError(Throwable error) {
        LOGGER.error("import error", error);

        // queue an update in the main gui
        Platform.runLater(() -> ErrorDialog.show(root, "Import fehlgeschlagen", "Fehlermeldung: " + error.getMessage()));
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
                            .sorted((p1, p2) -> p2.getDatetime().compareTo(p1.getDatetime()))
                            .collect(Collectors.toList()));
        } catch (ServiceException ex) {
            LOGGER.error("failed loading fotos", ex);
            ErrorDialog.show(root, "Laden von Fotos fehlgeschlagen",
                    "Fehlermeldung: " + ex.getMessage());
        }
    }
}
