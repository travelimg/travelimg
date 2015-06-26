package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.gui.control.skin.TagControlSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Control representing a tag that applies to one or more entities.
 */
public class TagControl extends Control {

    private static final Logger LOGGER = LogManager.getLogger();

    private Runnable onApply;
    private Runnable onRemove;

    public TagControl() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    /**
     * Name of the tag.
     */
    public final StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(this, "name");
        }
        return this.name;
    }
    private StringProperty name;
    public final String getName() { return nameProperty().get(); }
    public final void setName(String value) { nameProperty().set(value); }

    /**
     * Number of entities the tag applies to.
     *
     * Applies to all entities if set to -1.
     */
    public final IntegerProperty countProperty() {
        if (count == null) {
            count = new SimpleIntegerProperty(this, "count") {
                @Override protected void invalidated() {
                    updateStyleClass();
                    if (isApplied() && onApply != null) {
                        onApply.run();
                    }
                    if (isRemoved() && onRemove != null) {
                        onRemove.run();
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
     * Action invoked when the tag is applied to all entities.
     */
    public final void setOnApply(Runnable onApply) {
        this.onApply = onApply;
    }

    /**
     * Set the handler invoked when the tag is removed from the entities.
     */
    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    /**
     * Get a value indicating that the tag applies only to some entities.
     */
    public final boolean isPartial() {
        return !isApplied() && !isRemoved();
    }

    /**
     * Get a value indicating that the tag applies to all entities.
     */
    public final boolean isApplied() {
        return getCount() == -1;
    }

    /**
     * Get a value indicating that the tag applies to no entities.
     */
    public final boolean isRemoved() {
        return getCount() == 0;
    }

    /**
     * Apply the tag to all entities.
     */
    public void apply() {
        if (isApplied()) return;
        LOGGER.debug("applying tag {}", getName());
        setCount(-1);
    }

    /**
     * Remove the tag from the entities.
     */
    public void remove() {
        if (isRemoved()) return;
        LOGGER.debug("removing tag {}", getName());
        setCount(0);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new TagControlSkin(this);
    }

    private void updateStyleClass() {
        getStyleClass().removeAll(STYLE_CLASS_PARTIAL);
        getStyleClass().removeAll(STYLE_CLASS_APPLIED);
        if (isPartial()) {
            getStyleClass().add(STYLE_CLASS_PARTIAL);
        } else if (isApplied()) {
            getStyleClass().add(STYLE_CLASS_APPLIED);
        }
    }

    private static final String DEFAULT_STYLE_CLASS = "tag";
    private static final String STYLE_CLASS_PARTIAL = "partial";
    private static final String STYLE_CLASS_APPLIED = "applied";
}
