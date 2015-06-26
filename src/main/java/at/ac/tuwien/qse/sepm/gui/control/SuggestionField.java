package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.gui.control.skin.SuggestionFieldSkin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuggestionField extends Control {

    private static final Logger LOGGER = LogManager.getLogger();

    private ObservableList<String> suggestions = FXCollections.observableArrayList();
    private Runnable onAction;

    public SuggestionField() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    /**
     * Get a list of strings that are displayed as suggestions while the user types.
     *
     * @return list of suggestions
     */
    public final ObservableList<String> getSuggestions() {
        return this.suggestions;
    }

    /**
     * Text of the control for which suggestions are made.
     */
    public final StringProperty textProperty() {
        if (text == null) {
            text = new SimpleStringProperty(this, "text");
        }
        return text;
    }
    private StringProperty text;
    public final String getText() { return textProperty().get(); }
    public final void setText(String value) { textProperty().set(value); }

    /**
     * Label shown when there is no
     * @return
     */
    public final StringProperty labelProperty() {
        if (label == null) {
            label = new SimpleStringProperty(this, "label");
        }
        return label;
    }
    private StringProperty label;
    public final String getLabel() { return labelProperty().get(); }
    public final void setLabel(String value) { labelProperty().set(value); }


    /**
     * Get the best matching suggestion for the current text.
     *
     * @return best match or null, if none was found
     */
    public String getSuggestion() {
        if (getText() == null || getText().isEmpty()) return null;
        for (String s : getSuggestions()) {
            // NOTE: List may contain null values.
            if (s == null) {
                continue;
            }

            String suggestion = s.toLowerCase();
            String text = getText().toLowerCase();

            // NOTE: Suggesting the value the user as already typed in is pointless.
            if (suggestion.equals(text)) {
                continue;
            }

            if (suggestion.startsWith(text)) {
                return s;
            }
        }
        return null;
    }

    public void setOnAction(Runnable onAction) {
        this.onAction = onAction;
    }

    public void confirm() {
        if (onAction != null) {
            LOGGER.debug("running action with text {}", getText());
            onAction.run();
        }
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new SuggestionFieldSkin(this);
    }

    private static final String DEFAULT_STYLE_CLASS = "suggestion-field";
}
