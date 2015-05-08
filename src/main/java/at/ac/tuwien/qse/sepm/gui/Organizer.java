package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * GUI component that is used for browsing photos.
 *
 * TODO: Decide whether to call it Organizer or Browser or something else.
 */
public class Organizer extends BorderPane {

    @Autowired private ImportService importService;
    @Autowired private PhotoService photoService;

    @FXML private Button importButton;
    @FXML private Button presentButton;
    @FXML private ListView<Date> monthList;

    private final ObservableList<Date> months = FXCollections.observableArrayList();
    private final SortedList<Date> monthsSorted = new SortedList<>(months);

    private final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy MMM");

    public Organizer() {
        FXMLLoadHelper.load(this, this, Organizer.class, "view/Organizer.fxml");

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

        months.addAll(getAvailableMonths());
    }

    /**
     * Set of photos that match the current filter.
     */
    public final ObservableList<Photo> getActivePhotos() {
        return activePhotosProperty().get();
    }
    private final ObjectProperty<ObservableList<Photo>> activePhotos =
            new SimpleObjectProperty<>(this, "activePhotos");
    public final ObjectProperty<ObservableList<Photo>> activePhotosProperty() {
        return activePhotos;
    }

    private void handleImport(Event event) {
        ImportDialog dialog = new ImportDialog(this, "Fotos importieren");
        Optional<List<Photo>> photos = dialog.showForResult();
        if (!photos.isPresent()) return;

        // FIXME importService is not wired
        //importService.importPhotos(photos.get(),
        //        this::handleImportedPhoto,
        //        this::handleImportError);
    }
    private void handleImportedPhoto(Photo photo) {

        // Ignore photos that are not part of the current filter.
        if (monthList.getSelectionModel().isEmpty()) return;
        String photoMonth = monthFormat.format(photo.getExif().getDate());
        String activeMonth = monthFormat.format(monthList.getSelectionModel().getSelectedItem());
        if (photoMonth != activeMonth) return;

        getActivePhotos().add(photo);
    }
    private void handleImportError(Throwable error) {
        InfoDialog dialog = new InfoDialog(this, "Import Fehler");
        dialog.setError(true);
        dialog.setHeaderText("Import fehlgeschlagen");
        dialog.setContentText("Fehlermeldung: " + error.getMessage());
        dialog.showAndWait();
    }

    private void handlePresent(Event event) {
        // TODO
    }

    // TODO: get photos from service
    private List<Photo> getPhotosByMonth(Date date) {
        List<Photo> list = new LinkedList<>();
        return list;
    }

    // TODO: get months from service
    private List<Date> getAvailableMonths() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("y-M");
            List<Date> list = new LinkedList<>();
            list.add(format.parse("2015-12"));
            list.add(format.parse("2015-11"));
            list.add(format.parse("2015-10"));
            list.add(format.parse("2015-07"));
            list.add(format.parse("2015-06"));
            list.add(format.parse("2015-03"));
            list.add(format.parse("2014-08"));
            list.add(format.parse("2014-07"));
            list.add(format.parse("2014-02"));
            list.add(format.parse("2014-01"));
            list.add(format.parse("2012-10"));
            list.add(format.parse("2012-09"));
            list.add(format.parse("2012-07"));
            list.add(format.parse("2012-06"));
            list.add(format.parse("2012-05"));
            return list;
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
