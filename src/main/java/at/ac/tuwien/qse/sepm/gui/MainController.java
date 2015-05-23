package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the main view.
 */
public class MainController {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private PhotoService photoService;
    @Autowired private ImportService importService;
    @Autowired private PhotographerService photographerService;
    @Autowired private Organizer organizer;
    @Autowired private Inspector inspector;

    @FXML private BorderPane root;
    @FXML private ScrollPane gridContainer;

    private final PhotoGrid grid = new PhotoGrid();
    private PhotoFilter filter = new PhotoFilter();

    private Cancelable loadingTask;

    public void close() {
        grid.close();
    }

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

        organizer.setPresentAction(() -> {
            FullscreenWindow fullscreen = new FullscreenWindow();
            fullscreen.present(grid.getPhotos());
        });

        organizer.setFilterChangeAction(this::handleFilterChange);

        grid.setSelectionChangeAction((selection) -> {
            if (selection.size() == 0) return;
            inspector.setActivePhoto(selection.iterator().next());
        });

        // Apply the initial filter.
        reloadImages();
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

    /**
     * Called whenever a new photo is imported
     * @param photo The newly imported    photo
     */
    private void handleImportedPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {
            // Ignore photos that are not part of the current filter.
            if (!filter.matches(photo)) return;
            grid.addPhoto(photo);
        });
    }

    private void handleFilterChange(PhotoFilter filter) {
        this.filter = filter;
        reloadImages();
    }

    private void reloadImages() {
        try {
            grid.setPhotos(photoService.getAllPhotos(filter));
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
