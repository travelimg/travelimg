package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DeleteDialog extends ResultDialog<Boolean> {

    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private Label statusText;

    /**
     * {@inheritDoc}
     */
    public DeleteDialog(Node origin, int photoCount) {
        super(origin, "Fotos Löschen");
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/DeleteDialog.fxml");
        confirmButton.setOnAction(this::handleConfirm);
        cancelButton.setOnAction(this::handleCancel);

        String status = "%d Fotos ausgewählt";
        if (photoCount == 1) {
            status = "%d Foto ausgewählt";
        }
        statusText.setText(String.format(status, photoCount));
    }

    private void handleCancel(Event event){
        setResult(false);
        close();
    }

    private void handleConfirm(Event event) {
        setResult(true);
        close();
    }
}
