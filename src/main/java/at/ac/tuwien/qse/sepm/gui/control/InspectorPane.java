package at.ac.tuwien.qse.sepm.gui.control;

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
