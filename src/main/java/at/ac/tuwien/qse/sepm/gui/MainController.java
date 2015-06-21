package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.gui.controller.impl.SlideshowViewImpl;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Controller for the main view.
 */
public class MainController {

    @FXML
    private Tab grid;
    @FXML
    private Tab world;
    @FXML
    private Tab slide;
    @FXML
    private TabPane root;
    @Autowired
    private WorldmapView worldMapView;
    @Autowired
    private Inspector inspector;
    @Autowired
    private SlideshowViewImpl slideshowView;
    private EventHandler<javafx.scene.input.MouseEvent> ehandl;
    private GoogleMapsScene map;

    public MainController() {

    }

    @FXML
    private void initialize() {

    }

}
