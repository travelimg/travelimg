package at.ac.tuwien.qse.sepm.gui.controller.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
        flickrButton.setOnAction((e) -> listeners.forEach(l -> l.onFlickr(this)));
        journeyButton.setOnAction((e) -> listeners.forEach(l -> l.onJourney(this)));
        deleteButton.setOnAction((e) -> listeners.forEach(l -> l.onDelete(this)));
        exportButton.setOnAction((e) -> listeners.forEach(l -> l.onExport(this)));
        pageSelector.currentPageProperty().addListener((object, oldValue, newValue) ->
            listeners.forEach(l -> l.onPageSwitch(this)));
    }

    public Button getFlickrButton(){
        return flickrButton;
    }
}
