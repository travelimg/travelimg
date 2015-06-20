package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Slide;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlideTile extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();

    Integer id;
    public SlideTile(int id) {
        this.id = id;

        getStyleClass().add("tile");

        setOnDragDetected(this::handleDragDetected);
        setOnDragDone(this::handleDragDone);

        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
    }

    public Slide getSlide() {
        Slide slide = new Slide();
        slide.setId(id);
        return slide;
    }

    private void handleDragDetected(MouseEvent event) {
        LOGGER.debug("drag detected");
        getStyleClass().add("dragging");

        Dragboard db = startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString("slide: " + getSlide().getId().toString());
        db.setContent(content);

        event.consume();
    }

    private void handleDragDone(DragEvent event) {
        LOGGER.debug("drag done");
        getStyleClass().removeAll("dragging");
        event.consume();
    }
}
