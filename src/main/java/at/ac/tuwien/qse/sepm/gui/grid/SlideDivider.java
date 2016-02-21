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
                int slideId = Integer.parseInt(dragboard.getString());
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
