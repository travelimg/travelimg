package at.ac.tuwien.qse.sepm.gui.dialogs;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interface for a GUI component that can be opened as a dialog.
 */
public class Dialog extends Pane {

    private static final Logger logger = LogManager.getLogger();

    private final Stage stage = new Stage();

    public Dialog(Node origin, String title) {
        Stage owner = (Stage) origin.getScene().getWindow();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle(title);
        stage.setScene(new Scene(this));
    }

    protected Logger getLogger() {
        return logger;
    }

    public void showAndWait() {
        getLogger().debug("dialog opened");
        stage.showAndWait();
    }

    public void close() {
        getLogger().debug("dialog closed");
        stage.close();
    }
}
