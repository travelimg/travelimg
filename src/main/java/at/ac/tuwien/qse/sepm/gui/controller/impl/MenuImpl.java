package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.gui.control.PageSelector;
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedList;

public class MenuImpl implements Menu {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Collection<Listener> listeners = new LinkedList<>();

    @FXML
    private Button presentButton;

    @FXML
    private Button importButton;

    @FXML
    private Button flickrButton;

    @FXML
    private Button journeyButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button exportButton;

    @FXML
    private PageSelector pageSelector;

    @Override public void addListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.add(listener);
        LOGGER.debug("added listener {}", listener);
    }

    @Override public void removeListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.remove(listener);
        LOGGER.debug("removed listener {}", listener);
    }

    @Override public void setPageCount(int pageCount) {
        pageSelector.setPageCount(pageCount);
    }

    @Override public int getCurrentPage() {
        return pageSelector.getCurrentPage();
    }

    @FXML
    private void initialize() {
        presentButton.setOnAction((e) -> listeners.forEach(l -> l.onPresent(this)));
        importButton.setOnAction((e) -> listeners.forEach(l -> l.onImport(this)));
        flickrButton.setOnAction((e) -> listeners.forEach(l -> l.onFlickr(this)));
        journeyButton.setOnAction((e) -> listeners.forEach(l -> l.onJourney(this)));
        deleteButton.setOnAction((e) -> listeners.forEach(l -> l.onDelete(this)));
        exportButton.setOnAction((e) -> listeners.forEach(l -> l.onExport(this)));
        pageSelector.currentPageProperty().addListener((object, oldValue, newValue) ->
            listeners.forEach(l -> l.onPageSwitch(this)));
    }
}
