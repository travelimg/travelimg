package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ConfirmationDialog extends ResultDialog<Boolean> {

    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private Label descriptionLabel;

    /**
     * {@inheritDoc}
     */
    public ConfirmationDialog(Node origin, String title, String description) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/ConfirmationDialog.fxml");
        descriptionLabel.setText(description);
        confirmButton.setOnAction(this::handleConfirm);
        cancelButton.setOnAction(this::handleCancel);
    }

    private void handleCancel(Event event){
        setResult(false);
        close();
    }

    private void handleConfirm(Event event) {
        setResult(true);
        close();
    }

    public void setConfirmButtonText(String text){
        confirmButton.setText(text);
    }

    public void setCancelButtonText(String text){
        cancelButton.setText(text);
    }
}
