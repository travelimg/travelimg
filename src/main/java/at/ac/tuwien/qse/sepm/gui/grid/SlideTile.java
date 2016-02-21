package at.ac.tuwien.qse.sepm.gui.grid;

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
    private final Label caption = new Label();

    public SlideTile(Slide slide) {
        if (slide == null) throw new IllegalArgumentException();
        this.slide = slide;

        getStyleClass().add("tile");

        caption.getStyleClass().add("caption");
        caption.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setBottomAnchor(caption, 0.0);
        AnchorPane.setLeftAnchor(caption, 0.0);
        overlay.getStyleClass().add("overlay");
        overlay.getChildren().add(caption);
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
        boolean hasCaption = slide.getCaption() != null && !slide.getCaption().isEmpty();
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
