package at.ac.tuwien.qse.sepm.gui.dialogs;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A JavaFX pane that is opened in a separate stage.
 */
public class Dialog extends Pane {

    private static final Logger logger = LogManager.getLogger();

    private final Stage stage = new Stage();

    /**
     * @param origin node that provides the stage that serves as the owner of this dialog
     * @param title title displayed in the top bar of the dialog window
     */
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

    /**
     * Opens the dialog until {@link Dialog::close} is called.
     */
    public void showAndWait() {
        getLogger().debug("dialog opened");
        stage.showAndWait();
    }

    /**
     * Closes the dialog.
     */
    public void close() {
        getLogger().debug("dialog closed");
        stage.close();
    }
}
