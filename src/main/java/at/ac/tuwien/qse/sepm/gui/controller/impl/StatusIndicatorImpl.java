package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.gui.controller.StatusIndicator;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SynchronizationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

public class StatusIndicatorImpl implements StatusIndicator {

    private final List<ServiceException> errors = new LinkedList<>();

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
        syncService.subscribeError((operation, error) -> Platform.runLater(this::update));
        errorIndicator.setOnAction(event -> Platform.runLater(this::update));
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
