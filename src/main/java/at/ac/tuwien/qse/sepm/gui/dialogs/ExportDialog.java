package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.service.DropboxService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExportDialog extends ResultDialog<String> {

    private String exportFolder;
    private final int photoCount;
    @FXML
    private Button browseButton;
    @FXML
    private TextField directoryField;
    @FXML
    private Button exportButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button dropboxButton;
    @FXML
    private Label statusText;

    private Node root;

    private DropboxService dropboxService;

    /**
     * {@inheritDoc}
     */
    public ExportDialog(Node origin, DropboxService dropboxService, int photoCount) {
        super(origin, "Fotos exportieren");
        FXMLLoadHelper.load(this, this, ExportDialog.class, "view/ExportDialog.fxml");

        this.photoCount = photoCount;
        this.dropboxService = dropboxService;
        this.root = origin;

        browseButton.setOnAction(this::handleBrowse);
        exportButton.setOnAction(this::handleExport);
        cancelButton.setOnAction(this::handleCancel);
        dropboxButton.setOnAction(this::handleDropBox);


        updateStatus();
        directoryField.textProperty()
                .addListener((observable, oldValue, newValue) -> updateStatus());
    }

    private void handleDropBox(Event event) {
        String dropboxFolder = "";
        try {
            dropboxFolder = dropboxService.getDropboxFolder();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Export", "Konnte keinen Dropboxordner finden");
        }
        this.exportFolder = dropboxFolder;
        directoryField.setText(dropboxFolder);
        handleBrowse(event);
    }

    private void handleBrowse(Event event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Export Ort wählen");
        if(exportFolder != null) {
            directoryChooser.setInitialDirectory(new File(exportFolder));
        }
        File directory = directoryChooser.showDialog(null);

        if (directory == null || !directory.exists()) {
            getLogger().info("Invalid directory selected.");
            return;
        }

        directoryField.setText(directory.getPath());
        exportFolder = directory.getPath();
    }

    private void handleExport(Event event) {
        Path root = Paths.get(exportFolder);
        Path dest = Paths.get(directoryField.getText());

        Path relative = root.relativize(dest);

        setResult(exportFolder);

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
        String format = "%d Fotos ausgewählt";
        if (photoCount == 1) {
            format = "%d Foto ausgewählt";
        }
        statusText.setText(String.format(format, photoCount));
    }
}
