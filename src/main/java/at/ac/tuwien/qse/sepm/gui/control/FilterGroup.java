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

import at.ac.tuwien.qse.sepm.gui.control.skin.FilterGroupSkin;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@DefaultProperty("items")
public class FilterGroup<T> extends Control {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ObservableList<FilterControl<T>> items = FXCollections.observableArrayList();
    private final Map<FilterControl, InvalidationListener> listeners = new HashMap<>();
    private Runnable onUpdate;

    public FilterGroup() {
        getStyleClass().setAll("filter-group");

        // Always clean up the listeners.
        getItems().addListener((Observable observable) -> {
            for (FilterControl<T> item : getItems()) {
                InvalidationListener listener = listeners.get(item);
                if (listener != null) {
                    item.includedProperty().removeListener(listener);
                }
                listener = (Observable o) -> update();
                item.includedProperty().addListener(listener);
            }
        });
    }

    public final ObservableList<FilterControl<T>> getItems() {
        return this.items;
    }

    public final StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty(this, "title") {
                @Override protected void invalidated() {
                    updateCssClasses();
                }
            };
        }
        return title;
    }
    private StringProperty title;
    public final String getTitle() { return titleProperty().get(); }
    public final void setTitle(String value) { titleProperty().set(value); }

    public final BooleanProperty expandedProperty() {
        if (expanded == null) {
            expanded = new SimpleBooleanProperty(this, "expanded") {
                @Override protected void invalidated() {
                    updateCssClasses();
                }
            };
        }
        return expanded;
    }
    private BooleanProperty expanded;
    public final boolean isExpanded() { return expandedProperty().get(); }
    public final void setExpanded(boolean value) { expandedProperty().set(value); }

    /**
     * Set action invoked when any filter in the group changes.
     *
     * @param onUpdate action that is invoked
     */
    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    public boolean isAllIncluded() {
        return getIncludedValues().size() == getItems().size();
    }

    public boolean isAllExcluded() {
        return getExcludedValues().size() == getItems().size();
    }

    public boolean isIndetermined() {
        return !isAllExcluded() && !isAllIncluded();
    }

    public boolean isEmpty() {
        return getItems().isEmpty();
    }

    public Set<T> getValues() {
        return getItems().stream()
                .map(FilterControl::getValue)
                .collect(Collectors.toSet());
    }

    public Set<T> getIncludedValues() {
        return getItems().stream()
                .filter(FilterControl::isIncluded)
                .map(FilterControl::getValue)
                .collect(Collectors.toSet());
    }

    public Set<T> getExcludedValues() {
        return getItems().stream()
                .filter((item) -> !item.isIncluded())
                .map(FilterControl::getValue)
                .collect(Collectors.toSet());
    }

    public void toggleAll() {
        if (isIndetermined() || isAllExcluded()) {
            includeAll();
        } else {
            excludeAll();
        }
    }

    public void include(T value) {
        forItem(value, FilterControl::include);
    }

    public void exclude(T value) {
        forItem(value, FilterControl::exclude);
    }

    public void includeAll() {
        getItems().forEach(FilterControl::include);
    }

    public void excludeAll() {
        getItems().forEach(FilterControl::exclude);
    }

    public void expand() {
        setExpanded(true);
    }

    public void collapse() {
        setExpanded(false);
    }

    public void toggleExpansion() {
        setExpanded(!isExpanded());
    }

    private void forItem(T value, Consumer<FilterControl<T>> action) {
        getItems().forEach(item -> {
            if (item.getValue() == null) {
                if (value == null) {
                    action.accept(item);
                }
            } else {
                if (item.getValue().equals(value)) {
                    action.accept(item);
                }
            }
        });
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new FilterGroupSkin<>(this);
    }

    private void update() {
        updateCssClasses();
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    private void updateCssClasses() {
        getStyleClass().removeAll("indetermined", "all-included", "all-excluded", "expanded", "empty");
        if (isIndetermined()) getStyleClass().add("indetermined");
        if (isAllIncluded()) getStyleClass().add("all-included");
        if (isAllExcluded()) getStyleClass().add("all-excluded");
        if (isExpanded()) getStyleClass().add("expanded");
        if (isEmpty()) getStyleClass().add("empty");
    }
}
