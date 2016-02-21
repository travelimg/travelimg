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
import at.ac.tuwien.qse.sepm.gui.control.FilterGroup;
import at.ac.tuwien.qse.sepm.gui.control.OptionalContent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.*;

public class FilterGroupSkin<T> extends SkinBase<FilterGroup<T>> {

    private final VBox container = new VBox();

    private final BorderPane header = new BorderPane();
    private final HBox headerLeft = new HBox();
    private final FontAwesomeIconView icon = new FontAwesomeIconView();
    private final Label title = new Label();
    private final Button expand = new Button();
    private final FontAwesomeIconView expandIcon = new FontAwesomeIconView();

    private final OptionalContent body = new OptionalContent();
    private final Label placeholder = new Label();
    private final VBox list = new VBox();

    public FilterGroupSkin(FilterGroup<T> control) {
        super(control);

        icon.getStyleClass().setAll("check-icon");
        title.getStyleClass().setAll("title");
        expand.getStyleClass().setAll("expand-button");
        expandIcon.getStyleClass().setAll("icon");
        expand.setGraphic(expandIcon);
        headerLeft.getStyleClass().setAll("left");
        headerLeft.setAlignment(Pos.CENTER_LEFT);
        headerLeft.getChildren().addAll(icon, title);
        header.getStyleClass().setAll("header");
        header.setLeft(headerLeft);
        header.setRight(expand);

        placeholder.setText("Keine Optionen verfügbar.");

        list.getStyleClass().setAll("list");
        body.getStyleClass().setAll("body");
        body.setContent(list);
        body.setPlaceholder(placeholder);

        container.getChildren().addAll(header, body);
        getChildren().add(container);

        getSkinnable().titleProperty().addListener((observable) -> update());
        getSkinnable().expandedProperty().addListener((observable) -> update());
        getSkinnable().getItems().addListener(
                (ListChangeListener.Change<? extends FilterControl<T>> change) -> updateList());

        expand.setOnAction(event -> getSkinnable().toggleExpansion());
        header.setOnMouseClicked(event -> getSkinnable().toggleExpansion());
        icon.setOnMouseClicked(event -> {
            getSkinnable().toggleAll();
            event.consume();
        });

        update();
        updateList();
    }

    private void update() {
        // expand
        body.setVisible(getSkinnable().isExpanded());
        body.setManaged(getSkinnable().isExpanded());

        // title
        title.setText(getSkinnable().getTitle());
    }

    private void updateList() {
        body.setAvailable(!getSkinnable().getItems().isEmpty());
        list.getChildren().clear();
        for (FilterControl<T> filter : getSkinnable().getItems()) {
            list.getChildren().add(filter);
        }
    }
}
