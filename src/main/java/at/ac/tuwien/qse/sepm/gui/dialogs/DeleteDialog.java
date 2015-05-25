package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.List;

/**
 * Created by christoph on 20.05.15.
 */
public class DeleteDialog extends ResultDialog<List<Photo>> {

    @FXML private Button cancelButton;
    @FXML private Button deleteP;
    private List<Photo> photos;

    /**
     *
     * @param origin node that provides the stage that serves as the owner of this dialog
     * @param photos the photos to delete
     */
    public DeleteDialog(Node origin, List<Photo> photos) {
        super(origin, "Delete photos");
        this.photos = photos;
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/DeleteDialog.fxml");
        deleteP.setOnAction(this::handleDeleteP);
        cancelButton.setOnAction(this::handleCancel);
    }

    /**
     * close the DeleteDialog
     * @param event
     */
    private void handleCancel(Event event){
        close();
    }

    /**
     *  Delete the photos
     * @param event
     */
    private void handleDeleteP(Event event) {
        setResult(photos);
        close();
    }
}
