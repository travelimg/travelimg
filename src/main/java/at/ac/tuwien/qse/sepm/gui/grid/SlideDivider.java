package at.ac.tuwien.qse.sepm.gui.grid;

import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class SlideDivider extends Button {

    private static final Logger LOGGER = LogManager.getLogger();

    private Consumer<Integer> slideDroppedCallback = null;

    public SlideDivider() {
        setOnDragEntered(this::handleDragEntered);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        setOnDragExited(this::handleDragExited);
        setGraphic(new Rectangle(30, 150, Paint.valueOf("blue")));
    }

    public void setSlideDroppedCallback(Consumer<Integer> callback) {
        this.slideDroppedCallback = callback;
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
            try {
                int slideId = Integer.parseInt(dragboard.getString().replace("slide: ", ""));
                LOGGER.debug("dropped slide with id {} on divider", slideId);

                if (slideDroppedCallback != null) {
                    slideDroppedCallback.accept(slideId);
                }
            } catch (NumberFormatException ex) {
                success = false;
            }

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
