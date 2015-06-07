package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.scene.Node;

/**
 * Dialog that informs the user about an error.
 */
public class ErrorDialog extends InfoDialog {

    public static void show(Node origin, String header, String content) {
        ErrorDialog dialog = new ErrorDialog(origin, header, content);
        dialog.showAndWait();
    }

    public ErrorDialog(Node origin, String header, String content) {
        super(origin, "Fehler");

        setError(true);
        setHeaderText(header);
        setContentText(content);
    }
}
