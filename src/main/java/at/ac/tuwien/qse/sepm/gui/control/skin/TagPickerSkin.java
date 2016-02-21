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
import at.ac.tuwien.qse.sepm.gui.control.TagPicker;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class TagPickerSkin extends SkinBase<TagPicker> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final StackPane container;
    private final Label placeholder;
    private final VBox list;

    public TagPickerSkin(TagPicker control) {
        super(control);
        LOGGER.debug("creating instance");

        placeholder = new Label();
        placeholder.getStyleClass().setAll("placeholder");
        placeholder.setText("Keine Kategorien.");
        placeholder.setTextAlignment(TextAlignment.CENTER);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setMaxWidth(Double.MAX_VALUE);

        list = new VBox();
        list.getStyleClass().setAll("list");

        container = new StackPane();
        container.getChildren().addAll(placeholder, list);

        getChildren().add(container);

        getSkinnable().getEntities().addListener(
                (ListChangeListener.Change<? extends Set<String>> change) -> updateList());
        updateList();
    }

    private void updateList() {
        LOGGER.debug("updating tag picker skin with tags {}", getSkinnable().getTags());
        boolean hasTags = getSkinnable().hasTags();
        placeholder.setVisible(!hasTags);
        placeholder.setManaged(!hasTags);
        list.setVisible(hasTags);
        list.setManaged(hasTags);

        list.getChildren().clear();
        for (String tag : getSkinnable().getTagsSorted()) {
            TagControl item = new TagControl();
            item.setName(tag);
            item.setCount(getSkinnable().count(tag));
            if (getSkinnable().isApplied(tag)) {
                item.setCount(-1);
            }
            item.setOnApply(() -> {
                getSkinnable().apply(tag);
                updateList();
            });
            item.setOnRemove(() -> {
                getSkinnable().remove(tag);
                updateList();
            });
            VBox.setVgrow(item, Priority.ALWAYS);
            list.getChildren().add(item);
        }
    }
}
