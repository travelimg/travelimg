package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.gui.controller.impl.SlideshowViewImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Controller for the main view.
 */
public class MainController {

    private static final Logger LOGGER = LogManager.getLogger();
    @FXML
    private Tab grid;
    @FXML
    private Tab world;
    @FXML
    private Tab slide;
    @FXML
    private Tab highlights;
    @FXML
    private TabPane root;
    @Autowired
    private WorldmapView worldMapView;
    @Autowired
    private Inspector inspector;
    @Autowired
    private SlideshowViewImpl slideshowView;
    @Autowired
    private HighlightsViewController highlightsViewController;
    private EventHandler<javafx.scene.input.MouseEvent> ehandl;
    private GoogleMapsScene map;

    private boolean mapInitialized = false;

    @FXML
    private void initialize() {
        root.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(
                    ObservableValue<? extends Tab> ov,
                    Tab t, Tab t1) {
                if (t.equals(grid) && t1
                        .equals(world)) {
                    worldMapView
                            .setMap(inspector
                                    .getMap());
                }
                if (t.equals(world)
                        && t1
                        .equals(grid)) {
                    inspector
                            .setMap(worldMapView
                                    .getMap());
                }
                if (t1.equals(
                        highlights) && !mapInitialized) {
                    LOGGER.debug(
                            "SWITCH !!!! ");
                    highlightsViewController.setMap(new GoogleMapsScene());
                    highlightsViewController
                            .reloadJourneys();

                    mapInitialized = true;
                } else {

                    highlightsViewController
                            .reloadJourneys();
                    //highlightsViewController.reloadImages();
                }
            }
        });
    }

}
