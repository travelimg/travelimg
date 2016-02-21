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

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SlideGridNode extends HBox {

    private final SlideTile tile;
    private final SlideDivider divider = new SlideDivider();

    public SlideGridNode(SlideTile tile) {
        if (tile == null) throw new IllegalArgumentException();
        this.tile = tile;

        getStyleClass().add("node");

        getChildren().addAll(divider, tile);
        HBox.setHgrow(tile, Priority.ALWAYS);
        HBox.setHgrow(divider, Priority.ALWAYS);
    }

    public SlideTile getTile() {
        return tile;
    }

    public void setSlidePositionChangeCallback(BiConsumer<SlideTile, Integer> callback) {
        divider.setSlideDroppedCallback((sourceId) -> {
            if (sourceId.equals(tile.getSlide().getId())) {
                return; // ignore drop on itself
            }

            callback.accept(tile, sourceId);
        });
    }

    public void setSlideAddedCallback(Consumer<SlideGridNode> callback) {
        divider.setSlideAddedCallback(() -> callback.accept(this));
    }
}
