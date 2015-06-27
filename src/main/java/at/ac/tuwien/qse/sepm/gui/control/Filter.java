package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.gui.control.skin.FilterSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

/**
 * Control representing a filter for a single possible value for some attribute of an entity.
 *
 * For example, if the filtered entities are photos, each photo may have a field that contains the
 * name of the photographer. A single such name could be represented by this control. Including it
 * would mean, that the filter matches photos from this specific photographer. Excluding the name
 * would mean that photos of this photographer are not matched.
 *
 * Usually there would be one such control for every possible value of the filtered property.
 */
public class Filter<T> extends Control {

    private static final Logger LOGGER = LogManager.getLogger();

    public Filter() {
        getStyleClass().setAll("filter");
    }

    /**
     * Value indicating that entities with this value are matched.
     */
    public final BooleanProperty includedProperty() {
        if (included == null) {
            included = new SimpleBooleanProperty(this, "included") {
                @Override public void invalidated() {
                    update();
                }
            };
        }
        return included;
    }
    private BooleanProperty included;
    public final boolean isIncluded() { return includedProperty().get(); }
    public final void setIncluded(boolean value) { includedProperty().set(value); }

    /**
     * Number indicating how many entities in a result matched this specific filter. When included
     * is false this value obviously has not meaning, since the result of this filter would be empty.
     */
    public final IntegerProperty countProperty() {
        if (count == null) {
            count = new SimpleIntegerProperty(this, "count") {
                @Override public void invalidated() {
                    update();
                }
            };
        }
        return count;
    }
    private IntegerProperty count;
    public final int getCount() { return countProperty().get(); }
    public final void setCount(int value) { countProperty().set(value); }

    /**
     * Value this filter represents.
     */
    public final ObjectProperty<T> valueProperty() {
        if (value == null) {
            value = new SimpleObjectProperty<>(this, "value");
        }
        return value;
    }
    private ObjectProperty<T> value;
    public final T getValue() { return valueProperty().get(); }
    public final void setValue(T value) { valueProperty().set(value); }

    /**
     * Converter used for converting the value to a label text.
     */
    public final ObjectProperty<Function<T, String>> converterProperty() {
        if (converter == null) {
            converter = new SimpleObjectProperty<>(this, "converter");
        }
        return converter;
    }
    private ObjectProperty<Function<T, String>> converter;
    public final Function<T, String> getConverter() { return converterProperty().get(); }
    public final void setConverter(Function<T, String> value) { converterProperty().set(value); }

    /**
     * Value indicating that there is no photo in the result that matches the filter. This is the
     * case if {@link #isIncluded()} is {@code true} and {@link #getCount()} is zero or less.
     */
    public final boolean isInactive() {
        return isIncluded() && getCount() == 0;
    }

    public void toggle() {
        setIncluded(!isIncluded());
    }

    public void include() {
        setIncluded(true);
    }

    public void exclude() {
        setIncluded(false);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new FilterSkin<T>(this);
    }

    private void update() {
        getStyleClass().removeAll("included", "inactive");
        if (isIncluded()) {
            getStyleClass().add("included");
        }
        if (isInactive()) {
            getStyleClass().add("inactive");
        }
    }
}
