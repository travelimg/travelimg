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

import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tile extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();

    private boolean selected = false;

    /**
     * Marks this tile as selected. Has no effect if this tile is already selected.
     * Adds the 'selected' style class.
     */
    public void select() {
        if (selected) return;
        getStyleClass().add("selected");
        selected = true;
    }

    /**
     * Marks this tile as unselected. Has no effect if this tile is already unselected.
     * Removes the 'selected' style class.
     */
    public void deselect() {
        if (!selected) return;
        getStyleClass().removeAll("selected");
        selected = false;
    }

    /**
     * Get a value indicating that the tile is selected.
     *
     * @return true if it is selected, otherwise false
     */
    public boolean isSelected() {
        return selected;
    }
}
