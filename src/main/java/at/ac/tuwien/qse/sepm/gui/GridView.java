package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class GridView {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private PhotoService photoService;
    @Autowired private ImportService importService;
    @Autowired private PhotographerService photographerService;
    @Autowired private Organizer organizer;
    @Autowired private Inspector inspector;

    @FXML private BorderPane root;
    @FXML private ScrollPane gridContainer;

    private final ImageGrid<Photo> grid = new ImageGrid<>(PhotoGridTile::new);
    private final List<Photo> selection = new ArrayList<Photo>();
    private Predicate<Photo> filter = new PhotoFilter();

    @FXML
    private void initialize() {
        LOGGER.debug("initializing");

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
            // TODO
        });

        organizer.setPresentAction(() -> {
            FullscreenWindow fullscreen = new FullscreenWindow();
            fullscreen.present(grid.getItems());
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
        inspector.setUpdateHandler(photos -> {
            photos.stream()
                    .filter(filter.negate())
                    .forEach(grid::removeItem);
            photos.stream()
                    .filter(filter)
                    .forEach(grid::updateItem);
        });

        // Deleted photos are removed from the grid.
        inspector.setDeleteHandler(photos -> photos.forEach(grid::removeItem));

        // Apply the initial filter.
        handleFilterChange(organizer.getFilter());
    }

    private void handleImportError(Throwable error) {
        LOGGER.error("import error", error);

        // queue an update in the main gui
        Platform.runLater(() -> {
            InfoDialog dialog = new InfoDialog(root, "Import Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Import fehlgeschlagen");
            dialog.setContentText("Fehlermeldung: " + error.getMessage());
            dialog.showAndWait();
        });
    }

    public void deletePhotos(){
        for(Photo photo : selection){
            grid.removeItem(photo);
        }
        selection.clear();
    }

    /**
     * Called whenever a new photo is imported
     * @param photo The newly imported    photo
     */
    private void handleImportedPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {
            // Ignore photos that are not part of the current filter.
            if (!filter.test(photo)) return;
            grid.addItem(photo);
        });
    }

    private void handleFilterChange(PhotoFilter filter) {
        this.filter = filter;
        reloadImages();
    }

    private void reloadImages() {
        try {
            grid.setItems(photoService.getAllPhotos(filter));
        } catch (ServiceException ex) {
            LOGGER.error("failed loading fotos", ex);
            InfoDialog dialog = new InfoDialog(root, "Lade Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Laden von Fotos fehlgeschlagen");
            dialog.setContentText("Fehlermeldung: " + ex.getMessage());
            dialog.showAndWait();
        }
    }
}
