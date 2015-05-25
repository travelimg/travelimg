package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.util.ImageFileFilter;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExportDialog extends ResultDialog<String> {

    private final String dropboxFolder;

    @FXML private Button browseButton;
    @FXML private TextField directoryField;
    @FXML private Button exportButton;
    @FXML private Button cancelButton;
    @FXML private Label statusText;

    private final int photoCount;

    /**
     * {@inheritDoc}
     */
    public ExportDialog(Node origin, String dropboxFolder, int photoCount) {
        super(origin, "Fotos exportieren");
        FXMLLoadHelper.load(this, this, ExportDialog.class, "view/ExportDialog.fxml");

        this.dropboxFolder = dropboxFolder;
        this.photoCount = photoCount;

        directoryField.setText(dropboxFolder);
        browseButton.setOnAction(this::handleBrowse);
        exportButton.setOnAction(this::handleExport);
        cancelButton.setOnAction(this::handleCancel);

        updateStatus();
        directoryField.textProperty()
                .addListener((observable, oldValue, newValue) -> updateStatus());
    }

    private void handleBrowse(Event event) {

        // DirectoryChooser throws an exception if the directory does not exist.
        File currentDirectory = new File(directoryField.getText());
        if (!currentDirectory.exists()) {
            currentDirectory = new File(dropboxFolder);
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Export Ort wählen");
        directoryChooser.setInitialDirectory(currentDirectory);
        File directory = directoryChooser.showDialog(null);

        if (directory == null || !directory.exists()) {
            getLogger().info("Invalid directory selected.");
            return;
        }

        directoryField.setText(directory.getPath());
    }

    private void handleExport(Event event) {
        Path root = Paths.get(dropboxFolder);
        Path dest = Paths.get(directoryField.getText());

        Path relative = root.relativize(dest);

        setResult(relative.toString());

        close();
    }

    private void handleCancel(Event event) {
        close();
    }

    private void updateStatus() {

        exportButton.setDisable(true);

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

        // Export directory is valid.
        exportButton.setDisable(false);
        statusText.setText(String.format("%d Fotos ausgewählt", photoCount));
    }
}
