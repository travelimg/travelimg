package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.controller.GridView;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by christoph on 26.06.15.
 */
public class MainController {

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
    private Organizer organizer;
    @Autowired
    private GridView gridViewController;


    public void worldMapKlick(Place pl){
        organizer.setWorldMapPlace(pl);

        root.getSelectionModel().select(grid);

    }

}