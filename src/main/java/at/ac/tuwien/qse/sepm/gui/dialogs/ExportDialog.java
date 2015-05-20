package at.ac.tuwien.qse.sepm.gui.dialogs;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.service.DropboxService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.lang.invoke.SerializedLambda;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExportDialog extends ResultDialog<String> {

    private String dropboxFolder;

    @FXML
    private TextField directoryField;

    /**
     * {@inheritDoc}
     */
    public ExportDialog(Node origin, String dropboxFolder) {
        super(origin, "Fotos exportieren");
        FXMLLoadHelper.load(this, this, ExportDialog.class, "view/ExportDialog.fxml");

        this.dropboxFolder = dropboxFolder;
        directoryField.setText(dropboxFolder);
    }

    @FXML
    private void handleBrowse(Event event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Export Ort innerhalb von Dropbox wählen");
        directoryChooser.setInitialDirectory(new File(directoryField.getText()));
        File directory = directoryChooser.showDialog(null);

        if (directory == null || !directory.exists()) {
            getLogger().info("Invalid directory selected.");
            return;
        }

        directoryField.setText(directory.getPath());
    }


    @FXML
    private void handleCancel(Event event) {
        close();
    }

    @FXML
    private void handleExport(Event event) {
        Path root = Paths.get(dropboxFolder);
        Path dest = Paths.get(directoryField.getText());

        Path relative = root.relativize(dest);

        setResult(relative.toString());

        close();
    }

}
