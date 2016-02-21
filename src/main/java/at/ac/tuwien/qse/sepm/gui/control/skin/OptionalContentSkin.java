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

import at.ac.tuwien.qse.sepm.gui.control.OptionalContent;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class OptionalContentSkin extends SkinBase<OptionalContent> {

    private final HBox placeholder = new HBox();
    private final HBox content = new HBox();

    public OptionalContentSkin(OptionalContent control) {
        super(control);

        content.getStyleClass().setAll("content");
        placeholder.getStyleClass().setAll("placeholder");

        StackPane container = new StackPane();
        container.getStyleClass().setAll("container");
        container.getChildren().addAll(placeholder, content);
        getChildren().addAll(container);

        getSkinnable().contentProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().placeholderProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().availableProperty().addListener((observable, oldValue, newValue) -> update());

        update();
    }

    private void update() {
        placeholder.getChildren().clear();
        content.getChildren().clear();
        if (getSkinnable().getPlaceholder() != null) {
            HBox.setHgrow(getSkinnable().getPlaceholder(), Priority.ALWAYS);
            placeholder.getChildren().add(getSkinnable().getPlaceholder());
        }
        if (getSkinnable().getContent() != null) {
            HBox.setHgrow(getSkinnable().getContent(), Priority.ALWAYS);
            content.getChildren().add(getSkinnable().getContent());
        }
        placeholder.setVisible(!getSkinnable().getAvailable());
        content.setVisible(getSkinnable().getAvailable());
    }
}
