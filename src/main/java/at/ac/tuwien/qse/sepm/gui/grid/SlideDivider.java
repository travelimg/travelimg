package at.ac.tuwien.qse.sepm.gui.grid;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class SlideDivider extends Button {

    private static final Logger LOGGER = LogManager.getLogger();

    private final FontAwesomeIconView icon = new FontAwesomeIconView();

    private Consumer<Integer> slideDroppedCallback = null;
    private Runnable slideAddedCallback = null;

    public SlideDivider() {
        getStyleClass().add("divider");

        setOnDragEntered(this::handleDragEntered);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        setOnDragExited(this::handleDragExited);

        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        icon.setGlyphName("PLUS");
        icon.setTextAlignment(TextAlignment.CENTER);
        HBox.setHgrow(icon, Priority.ALWAYS);
        setGraphic(icon);

        setOnAction((event) -> {
            if (slideAddedCallback != null) {
                slideAddedCallback.run();
            }
        });
    }

    public void setSlideDroppedCallback(Consumer<Integer> callback) {
        this.slideDroppedCallback = callback;
    }

    public void setSlideAddedCallback(Runnable callback) {
        this.slideAddedCallback = callback;
    }

    private void handleDragEntered(DragEvent event) {
        LOGGER.debug("drag entered");
        getStyleClass().add("dropping");
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
        getStyleClass().removeAll("dropping");
        event.consume();
    }
}
