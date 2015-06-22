package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.gui.controller.impl.InspectorImpl;
import at.ac.tuwien.qse.sepm.gui.controller.impl.PhotoInspectorImpl;
import at.ac.tuwien.qse.sepm.gui.controller.impl.SlideshowViewImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private PhotoInspectorImpl inspector;
    @Autowired
    private SlideshowViewImpl slideshowView;
    private EventHandler<javafx.scene.input.MouseEvent> ehandl;
    private GoogleMapsScene map;

    public MainController() {

    }

    @FXML
    private void initialize() {
        root.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                                                                        @Override
                                                                        public void changed(ObservableValue<? extends Tab> ov, Tab t,
                                                                                            Tab t1) {
                                                                            if (t.equals(grid) && t1.equals(world)) {
                                                                                worldMapView.setMap(inspector.getMap());
                                                                            }
                                                                            if (t.equals(world) && t1.equals(grid)) {
                                                                                inspector.setMap(worldMapView.getMap());
                                                                            }
                                                                        }
                                                                    }
        );

    }

}
