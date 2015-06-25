package at.ac.tuwien.qse.sepm.gui.control;

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
