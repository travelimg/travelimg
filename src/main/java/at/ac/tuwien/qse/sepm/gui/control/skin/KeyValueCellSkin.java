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

import at.ac.tuwien.qse.sepm.gui.control.KeyValueCell;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;

public class KeyValueCellSkin extends SkinBase<KeyValueCell> {

    private final Label keyLabel = new Label();
    private final Label valueLabel = new Label();

    public KeyValueCellSkin(KeyValueCell control) {
        super(control);

        keyLabel.getStyleClass().setAll("key");
        valueLabel.getStyleClass().setAll("value");

        BorderPane container = new BorderPane();
        container.setLeft(keyLabel);
        container.setRight(valueLabel);

        getChildren().add(container);

        getSkinnable().keyProperty().addListener((obs, v1, v2) -> updateKey());
        getSkinnable().valueProperty().addListener((obs, v1, v2) -> updateValue());
        getSkinnable().indeterminedProperty().addListener((obs, v1, v2) -> updateIndetermined());

        update();
    }

    private void update() {
        updateKey();
        updateValue();
        updateIndetermined();
    }

    private void updateKey() {
        keyLabel.setText(getSkinnable().getKey());
    }

    private void updateValue() {
        String value = getSkinnable().getValue();
        if (value == null || value.isEmpty()) {
            value = "kein Wert";
        }
        valueLabel.setText(value);
    }

    private void updateIndetermined() {
        if (getSkinnable().isIndetermined()) {
            valueLabel.setText("mehrdeutig");
        }
    }
}
