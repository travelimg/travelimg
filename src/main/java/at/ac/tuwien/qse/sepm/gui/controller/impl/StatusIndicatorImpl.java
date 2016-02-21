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

import at.ac.tuwien.qse.sepm.gui.controller.StatusIndicator;
import at.ac.tuwien.qse.sepm.service.SynchronizationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

public class StatusIndicatorImpl implements StatusIndicator {

    private final List<Object> errors = new LinkedList<>();

    private State state = State.SYNCHRONIZED;

    @Autowired
    public SynchronizationService syncService;

    @FXML
    private Node root;

    @FXML
    private Node successIndicator;

    @FXML
    private Button errorIndicator;

    @FXML
    private Node progressIndicator;

    @FXML
    private void initialize() {
        errorIndicator.setVisible(false);
        progressIndicator.setVisible(false);

        syncService.subscribeQueue(operation -> Platform.runLater(this::update));
        syncService.subscribeComplete(operation -> Platform.runLater(this::update));
        syncService.subscribeError((operation, error) -> {
            errors.add(operation);
            Platform.runLater(this::update);
        });
        errorIndicator.setOnAction(event -> {
            errors.clear();
            Platform.runLater(this::update);
        });
    }

    private void update() {
        State state = checkState();
        if (state == this.state) return;

        this.state = state;

        successIndicator.setVisible(false);
        errorIndicator.setVisible(false);
        progressIndicator.setVisible(false);
        root.getStyleClass().removeAll("success", "progress", "error");

        switch (state) {
            case SYNCHRONIZED:
                successIndicator.setVisible(true);
                root.getStyleClass().add("success");
                break;
            case PROGRESS:
                progressIndicator.setVisible(true);
                root.getStyleClass().add("progress");
                break;
            case ERRORS:
                errorIndicator.setVisible(true);
                root.getStyleClass().add("error");
                break;
        }
    }

    private State checkState() {
        boolean inProgress = !syncService.getQueue().isEmpty();
        boolean hasErrors = !errors.isEmpty();
        if (inProgress) return State.PROGRESS;
        if (hasErrors) return State.ERRORS;
        return State.SYNCHRONIZED;
    }

    private enum State {
        SYNCHRONIZED,
        PROGRESS,
        ERRORS,
    }
}
