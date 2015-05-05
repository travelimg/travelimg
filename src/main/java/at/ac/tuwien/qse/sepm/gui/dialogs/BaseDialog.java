package at.ac.tuwien.qse.sepm.gui.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public abstract class BaseDialog extends Stage implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    private Stage parent;

    public BaseDialog(URL fxml, Stage parent) {
        this.parent = parent;

        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setController(this);

        try {
            Parent root = fxmlLoader.load();

            setScene(new Scene(root, 300, 350));
        } catch (IOException e) {
            logger.error("Failed to load ImportDialog.fxml", e);
            throw new RuntimeException(e);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public Stage getParent() {
        return parent;
    }

    @Override
    public abstract void initialize(URL location, ResourceBundle resources);
}


