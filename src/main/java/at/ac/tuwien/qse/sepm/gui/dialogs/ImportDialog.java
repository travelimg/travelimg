package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.util.ImageFileFilter;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImportDialog extends ResultDialog<List<Photo>> {

    @FXML private Button browseButton;
    @FXML private TextField directoryField;
    @FXML private TextField authorField;
    @FXML private Button importButton;
    @FXML private Button cancelButton;
    @FXML private Label statusText;

    private final List<Photo> photos = new LinkedList<>();

    public ImportDialog(Node origin, String title) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, ImportDialog.class, "view/ImportDialog.fxml");

        browseButton.setOnAction(this::handleBrowse);
        importButton.setOnAction(this::handleImport);
        cancelButton.setOnAction(this::handleCancel);

        updateStatus();
        directoryField.textProperty()
                .addListener((observable, oldValue, newValue) -> updateStatus());
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

        String author = authorField.getText();

        // Fetch all photos from the directory.
        ArrayList<Photo> photos = new ArrayList<>();
        for (final File file : directory.listFiles(new ImageFileFilter())) {
            if (!file.isDirectory()) {
                photos.add(new Photo(null, new Photographer(1, author), file.getPath(), 0));
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

        if (count == 1) {
            statusText.setText("1 Foto ausgewählt");
            return;
        }

        statusText.setText(String.format("%d Fotos ausgewählt", count));
        importButton.setDisable(false);
    }
}
