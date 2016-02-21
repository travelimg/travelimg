package at.ac.tuwien.qse.sepm.gui.control.skin;

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

import at.ac.tuwien.qse.sepm.gui.control.TagControl;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class TagControlSkin extends SkinBase<TagControl> {

    private static final String REMOVE_BUTTON_GLYPH_NAME = "TIMES";
    private static final String APPLIED_GLYPH_NAME = "CHECK_SQUARE";
    private static final String PARTIAL_GLYPH_NAME = "MINUS_SQUARE";
    private static final String REMOVED_GLYPH_NAME = "SQUARE";

    private final Label label;
    private final Button applyButton;
    private final FontAwesomeIconView icon;
    private final Button removeButton;

    public TagControlSkin(TagControl control) {
        super(control);

        icon = new FontAwesomeIconView();
        icon.getStyleClass().setAll("check-icon");
        icon.setGlyphSize(20);
        icon.setTextAlignment(TextAlignment.CENTER);

        applyButton = new Button();
        applyButton.getStyleClass().setAll("apply-button");
        applyButton.setGraphic(icon);

        label = new Label();
        label.getStyleClass().setAll("label");

        FontAwesomeIconView removeIcon = new FontAwesomeIconView();
        removeIcon.getStyleClass().setAll("remove-icon");
        removeIcon.setGlyphName(REMOVE_BUTTON_GLYPH_NAME);
        removeIcon.setGlyphSize(20);
        removeIcon.setFill(Color.GRAY);
        removeButton = new Button();
        removeButton.getStyleClass().setAll("remove-button");
        removeButton.setGraphic(removeIcon);
        getChildren().add(removeButton);

        HBox left = new HBox();
        left.getStyleClass().setAll("label-container");
        left.getChildren().addAll(applyButton, label);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox right = new HBox();
        right.getChildren().addAll(removeButton);
        right.setAlignment(Pos.CENTER_RIGHT);
        BorderPane container = new BorderPane();
        container.setLeft(left);
        container.setRight(right);
        getChildren().add(container);

        // Register listeners.
        control.nameProperty().addListener((observable, oldValue, newValue) -> update());
        control.countProperty().addListener((observable, oldValue, newValue) -> update());
        applyButton.setOnAction(this::handleApply);
        removeButton.setOnAction(this::handleRemove);

        update();
    }

    private void update() {
        String text = getSkinnable().getName();
        if (text == null) {
            text = "";
        }
        if (getSkinnable().isPartial()) {
            text += " (" + getSkinnable().getCount() + ")";
        }
        label.setText(text);

        if (getSkinnable().isPartial()) {
            icon.setGlyphName(PARTIAL_GLYPH_NAME);
        } else if (getSkinnable().isApplied()) {
            icon.setGlyphName(APPLIED_GLYPH_NAME);
        } else {
            icon.setGlyphName(REMOVED_GLYPH_NAME);
        }
    }

    private void handleApply(Event event) {
        event.consume();
        getSkinnable().apply();
    }

    private void handleRemove(Event event) {
        event.consume();
        getSkinnable().remove();
    }
}
