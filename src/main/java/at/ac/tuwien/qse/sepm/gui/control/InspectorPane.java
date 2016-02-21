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

import at.ac.tuwien.qse.sepm.gui.control.skin.InspectorPaneSkin;
import at.ac.tuwien.qse.sepm.gui.control.skin.OptionalContentSkin;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.DefaultProperty;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.*;

import java.util.Observable;

@DefaultProperty("body")
public class InspectorPane extends Control {

    public InspectorPane() {
        getStyleClass().add("inspector-pane");
    }

    /**
     * Plural name of the entities that the inspector operates on.
     */
    public final StringProperty entityNameProperty() {
        if (entityName == null) {
            entityName = new SimpleStringProperty(this, "entityName");
        }
        return entityName;
    }
    private StringProperty entityName;
    public final String getEntityName() { return entityNameProperty().get(); }
    public final void setEntityName(String value) { entityNameProperty().set(value); }

    /**
     * Number of entities the inspector currently operates on.
     */
    public final IntegerProperty countProperty() {
        if (count == null) {
            count = new SimpleIntegerProperty(this, "count") {
                @Override protected void invalidated() {
                    getStyleClass().removeAll("multiple");
                    if (get() > 1) {
                        getStyleClass().add("multiple");
                    }
                }
            };
        }
        return count;
    }
    private IntegerProperty count;
    public final int getCount() { return countProperty().get(); }
    public final void setCount(int value) { countProperty().set(value); }

    /**
     * Optional content on the top that does not scroll with the rest of the content.
     */
    public final ObjectProperty<Node> headerProperty() {
        if (header == null) {
            header = new SimpleObjectProperty<>(this, "header");
        }
        return header;
    }
    private ObjectProperty<Node> header;
    public final Node getHeader() { return headerProperty().get(); }
    public final void setHeader(Node value) { headerProperty().set(value); }

    /**
     * Main content, which will scroll vertically if it is too long to fit into the inspector.
     */
    public final ObjectProperty<Node> bodyProperty() {
        if (body == null) {
            body = new SimpleObjectProperty<>(this, "body");
        }
        return body;
    }
    private ObjectProperty<Node> body;
    public final Node getBody() { return bodyProperty().get(); }
    public final void setBody(Node value) { bodyProperty().set(value); }

    @Override protected Skin<?> createDefaultSkin() {
        return new InspectorPaneSkin(this);
    }
}
