package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.controller.*;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;


public class MainControllerImpl implements MainController{

    @FXML
    private Tab grid;
    @FXML
    private TabPane root;

    @Autowired
    private Organizer organizer;

    @Override public void showGridWithPlace(Place place) {
        organizer.setWorldMapPlace(place);
        root.getSelectionModel().select(grid);
    }
}