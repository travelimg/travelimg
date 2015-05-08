package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class InfoDialog extends Dialog {

    @FXML private Node root;

    @FXML private Label headerText;

    @FXML private Label contentText;

    @FXML private Button cancelButton;

    public InfoDialog(Node origin, String title) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/InfoDialog.fxml");

        cancelButton.setOnAction(this::handleCancel);
    }

    public String getHeaderText() {
        return headerTextProperty().get();
    }
    public StringProperty headerTextProperty() {
        return headerText.textProperty();
    }
    public void setHeaderText(String headerText) {
        headerTextProperty().set(headerText);
    }

    public String getContentText() {
        return contentTextProperty().get();
    }
    public StringProperty contentTextProperty() {
        return contentText.textProperty();
    }
    public void setContentText(String contentText) {
        contentTextProperty().set(contentText);
    }

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
