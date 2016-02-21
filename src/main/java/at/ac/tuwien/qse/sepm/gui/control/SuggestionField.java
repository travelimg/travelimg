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
