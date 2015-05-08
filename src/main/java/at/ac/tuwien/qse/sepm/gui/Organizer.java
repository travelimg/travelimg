package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.util.List;
import java.util.Optional;

public class Organizer extends BorderPane {

    @FXML private Button importButton;
    @FXML private Button presentButton;

    public Organizer() {
        FXMLLoadHelper.load(this, this, Organizer.class, "view/Organizer.fxml");

        importButton.setOnAction(this::handleImport);
        presentButton.setOnAction(this::handlePresent);
    }

    private void handleImport(Event event) {
        ImportDialog dialog = new ImportDialog(this, "Fotos importieren");
        Optional<List<Photo>> photos = dialog.showForResult();
        // TODO
    }

    private void handlePresent(Event event) {
        // TODO
    }
}
