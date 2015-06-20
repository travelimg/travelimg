package at.ac.tuwien.qse.sepm.gui.grid;

import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlideDivider extends Button {

    private static final Logger LOGGER = LogManager.getLogger();

    public SlideDivider() {
        setOnDragEntered(this::handleDragEntered);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        setOnDragExited(this::handleDragExited);
        setGraphic(new Rectangle(30, 150, Paint.valueOf("blue")));
    }

    private void handleDragEntered(DragEvent event) {
        LOGGER.debug("drag entered");
        getStyleClass().add("drag-entered");
        event.consume();
    }

    private void handleDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        LOGGER.debug("drag dropped");

        Dragboard dragboard = event.getDragboard();
        boolean success = dragboard.hasString();
        if (success) {
            LOGGER.debug("dropped {}", dragboard.getString());
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        LOGGER.debug("drag exited");
        getStyleClass().removeAll("drag-entered");
        event.consume();
    }
}
