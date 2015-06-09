package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Dialog that provides the user with a short message.
 */
public class InfoDialog extends Dialog {

    @FXML
    private Node root;

    @FXML
    private Label headerText;

    @FXML
    private Label contentText;

    @FXML
    private Button cancelButton;

    /**
     * {@inheritDoc}
     */
    public InfoDialog(Node origin, String title) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/InfoDialog.fxml");

        cancelButton.setOnAction(this::handleCancel);
    }

    /**
     * Text that is displayed in the header bar.
     * <p>
     * It should summarize the reason for the dialog in a few words.
     */
    public String getHeaderText() {
        return headerTextProperty().get();
    }

    public void setHeaderText(String headerText) {
        headerTextProperty().set(headerText);
    }

    public StringProperty headerTextProperty() {
        return headerText.textProperty();
    }

    /**
     * Text that is displayed in the body. This is usually the full error message.
     */
    public String getContentText() {
        return contentTextProperty().get();
    }

    public void setContentText(String contentText) {
        contentTextProperty().set(contentText);
    }

    public StringProperty contentTextProperty() {
        return contentText.textProperty();
    }

    /**
     * Value indicating that this dialog notifies about an error.
     */
    public boolean isError() {
        return root.getStyleClass().contains("error");
    }

    public void setError(boolean isError) {
        if (isError) {
            root.getStyleClass().add("error");
        } else {
            root.getStyleClass().remove("error");

        }
    }

    private void handleCancel(Event event) {
        close();
    }
}
