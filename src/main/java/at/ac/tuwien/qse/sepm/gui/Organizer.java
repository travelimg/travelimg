package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller for organizer view which is used for browsing photos by month.
 *
 * TODO: Decide whether to call it Organizer or Browser or something else.
 */
public class Organizer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private ImportService importService;
    @Autowired private PhotoService photoService;

    @Autowired private MainController mainController;

    @FXML private BorderPane root;
    @FXML private Button importButton;
    @FXML private Button presentButton;
    @FXML private ListView<Date> monthList;

    private final ObservableList<Date> months = FXCollections.observableArrayList();
    private final SortedList<Date> monthsSorted = new SortedList<>(months);
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy MMM");
    private Cancelable loadingTask;

    public Organizer() {

    }

    @FXML
    private void initialize() {
        importButton.setOnAction(this::handleImport);
        presentButton.setOnAction(this::handlePresent);

        monthList.setItems(monthsSorted);
        monthsSorted.setComparator((a, b) -> b.compareTo(a));

        monthList.setCellFactory(list -> new ListCell<Date>() {
            @Override protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) return;
                String monthString = monthFormat.format(item);
                setText(monthString);
            }
        });

        monthList.getSelectionModel().selectedItemProperty().addListener(this::handleMonthChange);

        months.addAll(getAvailableMonths());
    }

    private void handleImport(Event event) {
        ImportDialog dialog = new ImportDialog(root, "Fotos importieren");

        Optional<List<Photo>> photos = dialog.showForResult();
        if (!photos.isPresent()) return;

        importService.importPhotos(photos.get(),
                this::handleNewPhoto,
                this::handleImportError);
    }

    private void handleImportError(Throwable error) {
        LOGGER.error("Import error", error);

        // queue an update in the main gui
        Platform.runLater(() -> {
            InfoDialog dialog = new InfoDialog(root, "Import Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Import fehlgeschlagen");
            dialog.setContentText("Fehlermeldung: " + error.getMessage());
            dialog.showAndWait();
        });
    }

    private void handleLoadError(Throwable error) {
        LOGGER.error("Load error", error);

        // queue an update in the main gui
        Platform.runLater(() -> {
            InfoDialog dialog = new InfoDialog(root, "Lade Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Laden von Fotos fehlgeschlagen");
            dialog.setContentText("Fehlermeldung: " + error.getMessage());
            dialog.showAndWait();
        });
    }

    /**
     * Called whenever a new photo is loaded from the service layer
     * @param photo The newly loaded photo
     */
    private void handleNewPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {
            // Ignore photos that are not part of the current filter.
            if (monthList.getSelectionModel().isEmpty()) return;
            String photoMonth = monthFormat.format(photo.getExif().getDate());
            String activeMonth = monthFormat.format(monthList.getSelectionModel().getSelectedItem());

            if(!photoMonth.equals(activeMonth)) return;

            mainController.addPhoto(photo);
        });
    }

    private void handlePresent(Event event) {
        // TODO
    }

    private void handleMonthChange(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
        // remove active photos and replace them by
        // photos from the newly selected month
        if(loadingTask!=null){
            loadingTask.cancel();
        }
        mainController.clearPhotos();
        this.loadingTask = photoService.loadPhotosByDate(newValue, this::handleNewPhoto, this::handleLoadError);
    }

    // TODO: get months from service
    private List<Date> getAvailableMonths() {

        List<Date> months = new ArrayList<>();
        try {
            months = photoService.getMonthsWithPhotos();
        } catch (ServiceException ex) {
            // TODO: show error dialog
        }

        return months;
    }
}
