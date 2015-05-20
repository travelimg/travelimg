package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.dialogs.Dialog;import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by christoph on 20.05.15.
 */
public class DeleteDialog extends Dialog {
    @FXML private Node root;

    @FXML private Label headerText;

    @FXML private Label contentText;
    @FXML private Button cancelButton;
    @FXML private Button deleteP;
    @FXML private Button deleteA;
    @Autowired private PhotoService photoService;
    private Photo p;

    /**
     *
     * @param origin node that provides the stage that serves as the owner of this dialog
     * @param photoService photoservice to delete the photo
     * @param p the photo to delete
     */
    public DeleteDialog(Node origin, PhotoService photoService,Photo p) {
        super(origin, "delete Photo");
        FXMLLoadHelper.load(this, this, InfoDialog.class, "view/DeleteDialog.fxml");
        this.photoService = photoService;
        this.p = p;
        deleteP.setOnAction(this::handleDeleteP);

        cancelButton.setOnAction(this::handleCancel);
        headerText.textProperty().set("Foto löschen");
        contentText.textProperty().set("Möchten sie das ausgewählte Foto wirklich löschen?");
    }

    /**
     * close the DeleteDialog
     * @param event
     */
    private void handleCancel(Event event){
        close();
    }

    /**
     *  Delete the photo
     * @param event
     */
    private void handleDeleteP(Event event) {
        List<Photo> photos = new ArrayList<>();
        photos.add(p);
        try {
            photoService.deletePhotos(photos);
        } catch (ServiceException e) {
            //TODO Exception handling
        }
        close();
    }
}
