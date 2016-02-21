package at.ac.tuwien.qse.sepm.gui.control;

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

import at.ac.tuwien.qse.sepm.gui.control.skin.KeyValueCellSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class KeyValueCell extends Control {

    private static final String DEFAULT_STYLE_CLASS = "key-value-cell";

    public KeyValueCell() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        getStyleClass().add("empty");
    }

    /**
     * Text representing the key.
     */
    public final StringProperty keyProperty() {
        if (key == null) {
            key = new SimpleStringProperty(this, "key");
        }
        return key;
    }
    private StringProperty key;
    public final String getKey() { return keyProperty().get(); }
    public final void setKey(String value) { keyProperty().set(value); }

    /**
     * Text representing the value.
     */
    public final StringProperty valueProperty() {
        if (value == null) {
            value = new SimpleStringProperty(this, "value") {
                @Override protected void invalidated() {
                    if (get() == null || get().isEmpty()) {
                        getStyleClass().add("empty");
                    } else {
                        getStyleClass().removeAll("empty");
                    }
                }
            };
        }
        return value;
    }
    private StringProperty value;
    public final String getValue() { return valueProperty().get(); }
    public final void setValue(String value) { valueProperty().set(value); }

    /**
     * Flag indicating that the cell value is ambiguous.
     */
    public final BooleanProperty indeterminedProperty() {
        if (indetermined == null) {
            indetermined = new SimpleBooleanProperty(this, "indetermined") {
                @Override protected void invalidated() {
                    if (get()) {
                        getStyleClass().add("indetermined");
                    } else {
                        getStyleClass().removeAll("indetermined");
                    }
                }
            };
        }
        return indetermined;
    }
    private BooleanProperty indetermined;
    public final boolean isIndetermined() { return indeterminedProperty().get(); }
    public final void setIndetermined(boolean value) { indeterminedProperty().set(value); }

    @Override protected Skin<?> createDefaultSkin() {
        return new KeyValueCellSkin(this);
    }
}
