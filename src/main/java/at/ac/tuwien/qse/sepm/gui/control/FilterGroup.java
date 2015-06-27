package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.gui.control.skin.FilterGroupSkin;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Set;
import java.util.stream.Collectors;

@DefaultProperty("items")
public class FilterGroup<T> extends Control {

    private final ObservableList<Filter<T>> items = FXCollections.observableArrayList();
    private Runnable onUpdate;

    public FilterGroup() {
        getStyleClass().setAll("filter-group");
    }

    public final ObservableList<Filter<T>> getItems() {
        return this.items;
    }

    public final StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty(this, "title");
        }
        return title;
    }
    private StringProperty title;
    public final String getTitle() { return titleProperty().get(); }
    public final void setTitle(String value) { titleProperty().set(value); }

    public final BooleanProperty expandedProperty() {
        if (expanded == null) {
            expanded = new SimpleBooleanProperty(this, "expanded");
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
                .map(Filter::getValue)
                .collect(Collectors.toSet());
    }

    public Set<T> getIncludedValues() {
        return getItems().stream()
                .filter(Filter::isIncluded)
                .map(Filter::getValue)
                .collect(Collectors.toSet());
    }

    public Set<T> getExcludedValues() {
        return getItems().stream()
                .filter((item) -> !item.isIncluded())
                .map(Filter::getValue)
                .collect(Collectors.toSet());
    }

    public void include(T value) {
        getItems().forEach(item -> {
            if (item.getValue().equals(value)) {
                item.include();
            }
        });
    }

    public void exclude(T value) {
        getItems().forEach(item -> {
            if (item.getValue().equals(value)) {
                item.exclude();
            }
        });
    }

    public void includeAll() {
        getItems().forEach(Filter::include);
    }

    public void excludeAll() {
        getItems().forEach(Filter::exclude);
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

    public void onUpdate() {
        getStyleClass().removeAll("indetermined", "all-included", "all-excluded", "expanded", "empty");
        if (isIndetermined()) getStyleClass().add("indetermined");
        if (isAllIncluded()) getStyleClass().add("all-included");
        if (isAllExcluded()) getStyleClass().add("all-excluded");
        if (isExpanded()) getStyleClass().add("expanded");
        if (isEmpty()) getStyleClass().add("empty");

        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new FilterGroupSkin<>(this);
    }
}
