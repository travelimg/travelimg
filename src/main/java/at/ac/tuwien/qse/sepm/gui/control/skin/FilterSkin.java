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

import at.ac.tuwien.qse.sepm.gui.control.FilterControl;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

public class FilterSkin<T> extends SkinBase<FilterControl<T>> {

    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final Label label = new Label();


    public FilterSkin(FilterControl control) {
        super(control);

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        icon.getStyleClass().setAll("icon");
        container.getChildren().addAll(icon, label);
        getChildren().add(container);

        getSkinnable().setOnMouseClicked((event) -> handleClick());
        getSkinnable().valueProperty().addListener((observable) -> update());
        getSkinnable().includedProperty().addListener((observable) -> update());
        getSkinnable().countProperty().addListener((observable) -> update());
        getSkinnable().converterProperty().addListener((observable) -> update());
        update();
    }

    private void update() {

        String labelText = "Unbekannt";
        if (getSkinnable().getConverter() != null) {
            labelText = getSkinnable().getConverter().apply(getSkinnable().getValue());
        } else if (getSkinnable().getValue() != null) {
            labelText = getSkinnable().getValue().toString();
        }
        if (getSkinnable().isIncluded()) {
            labelText += String.format(" (%d)", getSkinnable().getCount());
        }
        label.setText(labelText);
    }

    private void handleClick() {
        getSkinnable().toggle();
    }
}
