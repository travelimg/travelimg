package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.controller.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;


public class MainControllerImpl implements MainController{

    @FXML
    private Tab grid;
    @FXML
    private TabPane root;
    @FXML
    private Tab highlights;
    @Autowired
    private Organizer organizer;
    @Autowired
    private HighlightsViewController highlightsViewController;
    @FXML
    private void initialize() {
        root.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

            @Override
            public void changed(ObservableValue<? extends Tab> arg0,
                    Tab arg1, Tab arg2) {
                if (arg2.getText().equals("highlights")) {
                    System.out.println("Test");
                    highlightsViewController.reloadJourneys();
                }
            }
        });
    }
    @Override public void showGridWithPlace(Place place) {
        organizer.setWorldMapPlace(place);
        root.getSelectionModel().select(grid);
    }
}