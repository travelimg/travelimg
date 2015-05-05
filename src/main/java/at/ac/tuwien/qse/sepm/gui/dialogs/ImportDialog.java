package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.ImageFileFilter;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ImportService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ImportDialog extends ResultDialog<List<Photo>> {

    @FXML
    private TextField directoryTextField;

    public ImportDialog(Stage parent) {
        super(ImportDialog.class.getClassLoader().getResource("dialogs/ImportDialog.fxml"), parent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitle("Import photos");
    }

    @FXML
    private void onSelectFolderClicked(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder");

        File directory = directoryChooser.showDialog(getParent());
        if(directory == null || !directory.exists()) {
            getLogger().info("Invalid directory selected.");
            return;
        }

        directoryTextField.setText(directory.getPath());
    }

    @FXML
    private void onImportClicked(ActionEvent event) {
        getLogger().info("Import initiated by user");

        File directory = new File(directoryTextField.getText());
        if(!directory.exists()) {
            getLogger().error("Import directory does not exist");
            return;
        }

        ArrayList<Photo> photos = new ArrayList<Photo>();
        for (final File file : directory.listFiles(new ImageFileFilter())) {
            if (!file.isDirectory()) {
                photos.add(new Photo(null, new Photographer(1, "TODO"), file.getPath(), new Date(file.lastModified()), 0));
            }
        }

        setResult(photos);

        close();
    }

    @FXML
    private void onCancelClicked(ActionEvent event) {
        getLogger().info("Import canceled");
        close();
    }
}
