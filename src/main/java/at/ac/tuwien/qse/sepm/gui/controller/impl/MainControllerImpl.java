package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.controller.MainController;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class MainControllerImpl implements MainController {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private Tab grid;
    @FXML
    private TabPane root;
    @FXML
    private Tab highlights;
    @FXML
    private Tab world;
    @Autowired
    private Organizer organizer;
    @Autowired
    private HighlightsViewControllerImpl highlightsViewController;
    @Autowired
    private WorldmapView worldmapView;

    @FXML
    private void initialize() {

    }

    @Override public void showGridWithPlace(Place place) {
        organizer.setWorldMapPlace(place);
        root.getSelectionModel().select(grid);
    }
}