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

import at.ac.tuwien.qse.sepm.gui.control.skin.OptionalContentSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Container that has content that is not always available. If it is not available a placeholder is
 * displayed instead.
 */
public class OptionalContent extends Control {

    /**
     * Placeholder for when the content is not active.
     */
    public final ObjectProperty<Node> placeholderProperty() { 
        if (placeholder == null) {
            placeholder = new SimpleObjectProperty<>(this, "placeholder");
        }
        return placeholder;
    }
    private ObjectProperty<Node> placeholder;
    public final Node getPlaceholder() { return placeholderProperty().get(); }
    public final void setPlaceholder(Node value) { placeholderProperty().set(value); }

    /**
     * Content of the pane.
     */
    public final ObjectProperty<Node> contentProperty() { 
        if (content == null) {
            content = new SimpleObjectProperty<>(this, "content");
        }
        return content;
    }
    private ObjectProperty<Node> content;
    public final Node getContent() { return contentProperty().get(); }
    public final void setContent(Node value) { contentProperty().set(value); }

    /**
     * Value indicating that the content is available and the placeholder should be hidden.
     */
    public final BooleanProperty availableProperty() {
        if (available == null) {
            available = new SimpleBooleanProperty(this, "available");
        }
        return available;
    }
    private BooleanProperty available;
    public final boolean getAvailable() { return availableProperty().get(); }
    public final void setAvailable(boolean value) { availableProperty().set(value); }

    @Override protected Skin<?> createDefaultSkin() {
        return new OptionalContentSkin(this);
    }
}
