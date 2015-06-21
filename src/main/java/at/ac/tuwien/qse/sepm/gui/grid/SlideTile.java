package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Slide;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlideTile extends Tile {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Slide slide;
    private final Pane border = new Pane();
    private final AnchorPane overlay = new AnchorPane();
    private final Label slideNumber = new Label();
    private final Label caption = new Label();

    public SlideTile(Slide slide) {
        if (slide == null) throw new IllegalArgumentException();
        this.slide = slide;

        getStyleClass().add("tile");

        slideNumber.getStyleClass().add("number");
        AnchorPane.setLeftAnchor(slideNumber, 0.0);
        AnchorPane.setTopAnchor(slideNumber, 0.0);
        caption.getStyleClass().add("caption");
        caption.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setBottomAnchor(caption, 0.0);
        AnchorPane.setLeftAnchor(caption, 0.0);
        overlay.getStyleClass().add("overlay");
        overlay.getChildren().addAll(slideNumber, caption);
        border.getStyleClass().add("border");
        getChildren().addAll(border, overlay);

        setOnDragDetected(this::handleDragDetected);
        setOnDragDone(this::handleDragDone);

        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        update();
    }

    public Slide getSlide() {
        return slide;
    }

    public void update() {
        slideNumber.setText(slide.getOrder().toString());
        boolean hasCaption = slide.getCaption() != null;
        caption.setVisible(hasCaption);
        caption.setText(slide.getCaption());
    }

    private void handleDragDetected(MouseEvent event) {
        LOGGER.debug("drag detected");
        getStyleClass().add("dragging");

        Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(getSlide().getId().toString());
        dragboard.setContent(content);

        event.consume();
    }

    private void handleDragDone(DragEvent event) {
        LOGGER.debug("drag done");
        getStyleClass().removeAll("dragging");
        event.consume();
    }
}
