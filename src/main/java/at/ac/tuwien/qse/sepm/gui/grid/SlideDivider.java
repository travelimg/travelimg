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

public class SlideDivider extends Button {

    private static final Logger LOGGER = LogManager.getLogger();

    private final FontAwesomeIconView icon = new FontAwesomeIconView();

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
            LOGGER.debug("dropped {}", dragboard.getString());
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
