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
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ImportDialog extends Stage implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    private ImportService importService;

    private Stage parent;

    @FXML
    private TextField directoryTextField;

    public ImportDialog(ImportService importService, Stage parent) {
        this.importService = importService;
        this.parent = parent;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dialogs/ImportDialog.fxml"));
        fxmlLoader.setController(this);

        try {
            Parent root = fxmlLoader.load();

            setScene(new Scene(root, 300, 350));
        } catch (IOException e) {
            logger.error("Failed to load ImportDialog.fxml", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitle("Import photos");
    }

    @FXML
    private void onSelectFolderClicked(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder");

        File directory = directoryChooser.showDialog(parent);
        if(directory == null || !directory.exists()) {
            logger.info("Invalid directory selected.");
            return;
        }

        directoryTextField.setText(directory.getPath());
    }

    @FXML
    private void onImportClicked(ActionEvent event) {
        logger.info("Import initiated by user");

        File directory = new File(directoryTextField.getText());
        if(!directory.exists()) {
            logger.error("Import directory does not exist");
            return;
        }

        ArrayList<Photo> photos = new ArrayList<Photo>();
        for (final File file : directory.listFiles(new ImageFileFilter())) {
            if (!file.isDirectory()) {
                photos.add(new Photo(null, new Photographer(null, "TODO"), file.getPath(), new Date(file.lastModified()), 0));
            }
        }

        try {
            importService.importPhotos(photos);
        } catch(ServiceException e) {
            logger.error("Failed to import photos", e);
            // TODO: notify user about error
        }

        logger.info("Successfully imported images");

        close();
    }

    @FXML
    private void onCancelClicked(ActionEvent event) {
        logger.info("Import canceled");
        close();
    }


}
