package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController extends BorderPane {

    private static final Logger logger = LogManager.getLogger();

    @FXML private Button importButton;
    @FXML private Button presentButton;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    public MainController() {
        FXMLLoadHelper.load(this, this, MainController.class, "view/Main.fxml");

        importButton.setOnAction(this::handleImport);
        presentButton.setOnAction(this::handlePresent);
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
    }

    private void handleImport(Event event) {
        // TODO
    }

    private void handlePresent(Event event) {
        // TODO
    }

    private void handleDelete(Event event) {
        // TODO
    }

    private void handleCancel(Event event) {
        // TODO
    }

    private void handleConfirm(Event event) {
        // TODO
    }
}
