package at.ac.tuwien.qse.sepm.gui.control;

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
