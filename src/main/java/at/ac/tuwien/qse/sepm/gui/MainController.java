package at.ac.tuwien.qse.sepm.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController extends BorderPane {

    private static final Logger logger = LogManager.getLogger();

    private final Organizer organizer = new Organizer();
    private final Inspector inspector = new Inspector();

    @FXML private GridPane imageGrid;

    public MainController() {
        FXMLLoadHelper.load(this, this, MainController.class, "view/Main.fxml");

        setLeft(organizer);
        setRight(inspector);
    }
}
