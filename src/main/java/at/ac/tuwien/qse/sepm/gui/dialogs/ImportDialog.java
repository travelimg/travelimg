package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.ImageFileFilter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Dialog that returns a set of photos that should be imported.
 */
public class ImportDialog extends ResultDialog<List<Photo>> {

    private PhotographerService photographerService;

    @FXML private Button browseButton;
    @FXML private TextField directoryField;
    @FXML private Button importButton;
    @FXML private Button cancelButton;
    @FXML private Label statusText;
    @FXML private Button addPhotographerButton;
    @FXML private ComboBox<Photographer> photographerBox;

    private final List<Photo> photos = new LinkedList<>();
    private ObservableList<Photographer> photographers = FXCollections.observableArrayList();

    /**
     * {@inheritDoc}
     */
    public ImportDialog(Node origin, PhotographerService photographerService) {
        super(origin, "Fotos importieren");
        FXMLLoadHelper.load(this, this, ImportDialog.class, "view/ImportDialog.fxml");

        this.photographerService = photographerService;

        browseButton.setOnAction(this::handleBrowse);
        importButton.setOnAction(this::handleImport);
        cancelButton.setOnAction(this::handleCancel);

        updateStatus();
        directoryField.textProperty()
                .addListener((observable, oldValue, newValue) -> updateStatus());

        photographerBox.setConverter(new StringConverter<Photographer>() {
            @Override
            public String toString(Photographer object) {
                if (object == null)
                    return "";
                return object.getName();
            }

            @Override
            public Photographer fromString(String string) {
                int id = photographerBox.getSelectionModel().getSelectedItem().getId();
                return new Photographer(id, string);
            }
        });

        addPhotographerButton.setOnAction(this::addPhotographer);
        photographerBox.valueProperty().addListener(this::handlePhotographerChanged);

        try {
            photographers.addAll(photographerService.readAll());
        } catch (ServiceException ex) {
            // TODO: show error dialog
        }

        photographerBox.setItems(photographers);

        // select the first photographer
        if(photographers.size() > 0) {
            photographerBox.getSelectionModel().select(0);
        }
    }

    private void handleBrowse(Event event) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Import Verzeichnis wählen");
        File directory = directoryChooser.showDialog(null);

        if (directory == null || !directory.exists()) {
            getLogger().info("Invalid directory selected.");
            return;
        }

        directoryField.setText(directory.getPath());
    }

    private void handleImport(Event event) {
        getLogger().info("Import initiated by user");

        File directory = new File(directoryField.getText());
        assert (directory.exists()); // import button should be disabled otherwise

        Photographer photographer = photographerBox.getSelectionModel().getSelectedItem();

        // Fetch all photos from the directory.
        ArrayList<Photo> photos = new ArrayList<>();
        for (final File file : directory.listFiles(new ImageFileFilter())) {
            if (!file.isDirectory()) {
                photos.add(new Photo(null, photographer, file.getPath(), 0, null, 0.0, 0.0));
            }
        }

        setResult(photos);
        close();
    }

    private void handleCancel(Event event) {
        close();
    }

    private void updateStatus() {

        importButton.setDisable(true);

        String path = directoryField.getText();
        if (path.isEmpty()) {
            statusText.setText("Kein Ordner angegeben");
            return;
        }

        File directory = new File(path);
        if (!directory.exists()) {
            statusText.setText("Ordner nicht gefunden");
            return;
        }

        int count = directory.listFiles(new ImageFileFilter()).length;
        if (count == 0) {
            statusText.setText("Keine Fotos im Ordner");
            return;
        }

        // There is at least one photo ready for import.
        importButton.setDisable(false);

        if (count == 1) {
            statusText.setText("1 Foto ausgewählt");
            return;
        }

        statusText.setText(String.format("%d Fotos ausgewählt", count));
    }

    @FXML private void addPhotographer(Event event) {
        try {
            // add new photographer to application
            Photographer photographer = photographerService.create(new Photographer(-1, "Neuer Fotograf"));

            photographers.add(photographer);
            photographerBox.getSelectionModel().select(photographer);
        } catch (ServiceException ex) {
            // TODO
        }
    }

    private void handlePhotographerChanged(ObservableValue<? extends Photographer> observable, Photographer oldValue, Photographer newValue) {
        if(oldValue == null) return;
        if(oldValue.equals(newValue)) return;
        if(oldValue.getId() != newValue.getId()) return;

        // update name of photographer
        try {
            photographerService.update(newValue);

            // update list with new photographer
            int index = photographerBox.getSelectionModel().getSelectedIndex();
            photographers.set(index, newValue);
        } catch (ServiceException ex) {
            // TODO
        }
    }
}
