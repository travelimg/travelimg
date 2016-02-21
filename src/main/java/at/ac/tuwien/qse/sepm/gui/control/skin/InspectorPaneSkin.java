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

import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.control.OptionalContent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InspectorPaneSkin extends SkinBase<InspectorPane> {

    private final OptionalContent root = new OptionalContent();
    private final VBox header = new VBox();
    private final ScrollPane body = new ScrollPane();
    private final Label placeholderLabel = new Label();
    private final VBox selectionInfo = new VBox();
    private final Label selectionInfoLabel = new Label();

    public InspectorPaneSkin(InspectorPane control) {
        super(control);

        VBox placeholder = new VBox();
        placeholder.setAlignment(Pos.CENTER);
        FontAwesomeIconView placeholderIcon = new FontAwesomeIconView();
        placeholderIcon.setGlyphName("CAMERA");
        placeholder.getChildren().addAll(placeholderIcon, placeholderLabel);

        selectionInfo.getStyleClass().add("selection-info");
        selectionInfo.setAlignment(Pos.CENTER);
        VBox.setVgrow(selectionInfoLabel, Priority.ALWAYS);
        selectionInfo.getChildren().add(selectionInfoLabel);

        header.getStyleClass().setAll("header");

        body.getStyleClass().setAll("body");
        body.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        body.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        root.setPlaceholder(placeholder);
        root.setContent(new VBox(header, body));
        getChildren().add(root);

        getSkinnable().headerProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().bodyProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().entityNameProperty().addListener((observable, oldValue, newValue) -> update());
        getSkinnable().countProperty().addListener((observable, oldValue, newValue) -> update());

        update();
    }

    private void update() {
        header.getChildren().setAll(selectionInfo);
        if (getSkinnable().getHeader() != null) {
            header.getChildren().add(getSkinnable().getHeader());
        }
        body.setContent(getSkinnable().getBody());

        String entityName = getSkinnable().getEntityName();
        if (entityName == null || entityName.isEmpty()) {
            entityName = "Elemente";
        }
        int count = getSkinnable().getCount();
        placeholderLabel.setText(String.format("Keine %s ausgewählt.", entityName));
        root.setAvailable(count != 0);
        selectionInfo.setVisible(count > 1);
        selectionInfo.setManaged(count > 1);
        selectionInfoLabel.setText(String.format("%d %s ausgewählt", count, entityName));
    }
}
