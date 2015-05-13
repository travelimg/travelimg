package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
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
    @Autowired private PhotographerService photographerService;

    @Autowired private MainController mainController;

    @FXML private BorderPane root;
    @FXML private Button importButton;
    @FXML private Button presentButton;
    @FXML private ListView<Date> monthList;

    private final ObservableList<Date> months = FXCollections.observableArrayList();
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy MMM");
    private final MonthSelector monthSelector = new MonthSelector(null);
    private Cancelable loadingTask;

    public Organizer() {

    }

    @FXML
    private void initialize() {
        importButton.setOnAction(this::handleImport);
        presentButton.setOnAction(this::handlePresent);

        SortedList<Date> monthsSorted = new SortedList<Date>(months);
        monthsSorted.setComparator((a, b) -> b.compareTo(a));
        monthList.setItems(monthsSorted);

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

    public void reloadPhotos(){
        Date selected = monthList.getSelectionModel().getSelectedItem();
        handleMonthChange(null, null, selected);
    }

    private void handleImport(Event event) {
        ImportDialog dialog = new ImportDialog(root, photographerService);

        Optional<List<Photo>> photos = dialog.showForResult();
        if (!photos.isPresent()) return;

        importService.importPhotos(photos.get(), this::handleImportedPhoto, this::handleImportError);
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
            }
        );
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
     * Called whenever a new photo is imported
     * @param photo The newly imported    photo
     */
    private void handleImportedPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {
            updateMonthListWithDate(photo.getExif().getDate());

            // Ignore photos that are not part of the current filter.
            if (!monthSelector.matches(photo)) {
                return;
            }

            mainController.addPhoto(photo);
        });
    }

    /**
     * Called whenever a new photo is loaded from the service layer
     * @param photo The newly loaded photo
     */
    private void handleLoadedPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {
                // Ignore photos that are not part of the current filter.
                if(!monthSelector.matches(photo))
                    return;

                mainController.addPhoto(photo);
            }
        );
    }

    /**
     * Show the current photo selection in fullscreen.
     * @param event The event triggering the request.
     */
    private void handlePresent(Event event) {
        // TODO
    }

    /**
     * Display photos for a newly selected month
     *
     * @param observable The observable value which changed
     * @param oldValue The previously selected month
     * @param newValue The newly selected month
     */
    private void handleMonthChange(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
        // cancel an older ongoing loading task
        if(loadingTask != null) {
            loadingTask.cancel();
        }

        monthSelector.setMonth(newValue);

        // remove currently active photos
        mainController.clearPhotos();

        // load photos from current month
        this.loadingTask = photoService.loadPhotosByDate(newValue, this::handleLoadedPhoto, this::handleLoadError);
    }

    /**
     * Add a new month to the list if it is not already included.
     * @param date represents the month to add
     */
    private void updateMonthListWithDate(Date date) {
        Date month = new Date(date.getYear(), date.getMonth(), 1);

        if(!months.contains(month)) {
            months.add(month);
        }
    }

    /**
     * Get a list of months for which we currently possess photos.
     * @return A list of months for which photos are available
     */
    private List<Date> getAvailableMonths() {
        List<Date> months = new ArrayList<>();
        try {
            months = photoService.getMonthsWithPhotos();
        } catch (ServiceException ex) {
            // TODO: show error dialog
        }

        return months;
    }

    /**
     * Matches all photos that where taken in the same month as the current active month
     */
    private class MonthSelector implements PhotoSelector {
        private Date month;

        public MonthSelector(Date month) {
            this.month = month;
        }

        public void setMonth(Date month) {
            this.month = month;
        }

        @Override
        public boolean matches(Photo photo) {
            if(month == null) {
                return false;
            }

            Date photoDate = photo.getExif().getDate();
            return month.equals(new Date(photoDate.getYear(), photoDate.getMonth(), 1));
        }
    }
}
