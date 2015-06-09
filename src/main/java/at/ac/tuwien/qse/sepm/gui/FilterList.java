package at.ac.tuwien.qse.sepm.gui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FilterList<E> extends VBox {

    private final CheckItem header = new CheckItem();
    private final Function<E, String> valueConverter;
    private final Map<E, CheckItem> items = new IdentityHashMap<>();

    private Consumer<List<E>> changeHandler;
    private boolean suppressChangeEvents = false;

    public FilterList(Function<E, String> valueConverter) {
        if (valueConverter == null) throw new IllegalArgumentException();
        this.valueConverter = valueConverter;

        getStyleClass().add("filter-list");

        header.getStyleClass().add("header");
        setVgrow(header, Priority.ALWAYS);
        getChildren().add(header);

        // Control the whole list via the header.
        header.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case CHECKED:
                    checkAll();
                    break;
                case UNCHECKED:
                    uncheckAll();
                    break;
            }
        });
    }

    public void setTitle(String title) {
        header.setText(title);
    }

    public List<E> getValues() {
        return new ArrayList<>(items.keySet());
    }

    public void setValues(List<E> values) {
        items.clear();
        getChildren().clear();
        getChildren().add(header);
        for (E value : values) {
            CheckItem item = new CheckItem();
            getChildren().add(item);
            setVgrow(item, Priority.ALWAYS);
            item.setText(valueConverter.apply(value));
            item.stateProperty().addListener((observable, oldValue, newValue) -> onChange());
            this.items.put(value, item);
        }
    }

    public List<E> getChecked() {
        return items.entrySet().stream()
                .filter(entry -> entry.getValue().getState() == CheckState.CHECKED)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    public void check(E value) {
        if (!items.containsKey(value)) return;
        items.get(value).setState(CheckState.CHECKED);
    }

    public void uncheck(E value) {
        if (!items.containsKey(value)) return;
        items.get(value).setState(CheckState.UNCHECKED);
    }

    public void checkAll(List<E> values) {
        if (values == null) throw new IllegalArgumentException();
        values.forEach(this::check);
    }

    public void uncheckAll(List<E> values) {
        if (values == null) throw new IllegalArgumentException();
        values.forEach(this::uncheck);
    }

    public void checkAll() {
        suppressChangeEvents = true;
        items.keySet().forEach(this::check);
        suppressChangeEvents = false;
        onChange();
    }

    public void uncheckAll() {
        suppressChangeEvents = true;
        items.keySet().forEach(this::uncheck);
        suppressChangeEvents = false;
        onChange();
    }

    public void setChangeHandler(Consumer<List<E>> changeHandler) {
        this.changeHandler = changeHandler;
    }

    private void onChange() {
        if (changeHandler != null && !suppressChangeEvents) {
            changeHandler.accept(getChecked());
        }
        updateHeader();
    }

    private void updateHeader() {
        if (getChecked().size() == items.size()) {
            header.setState(CheckState.CHECKED);
        } else if (getChecked().size() == 0) {
            header.setState(CheckState.UNCHECKED);
        } else {
            header.setState(CheckState.INDETERMINED);
        }
    }

    private enum CheckState {
        UNCHECKED,
        CHECKED,
        INDETERMINED
    }

    private static class CheckItem extends HBox {

        private static final Logger LOGGER = LogManager.getLogger();

        private final FontAwesomeIconView icon = new FontAwesomeIconView();
        private final Label label = new Label();
        private final ObjectProperty<CheckState> stateProperty =
                new SimpleObjectProperty<>(this, "state", CheckState.UNCHECKED);

        public CheckItem() {
            getStyleClass().add("check-item");
            getChildren().addAll(icon, label);

            setAlignment(Pos.CENTER_LEFT);

            setOnMouseClicked(this::handleClick);
            update();
        }

        public ObjectProperty<CheckState> stateProperty() {
            return stateProperty;
        }

        public CheckState getState() {
            return stateProperty().get();
        }

        public void setState(CheckState state) {
            stateProperty().set(state);
            update();
        }

        public void setText(String text) {
            label.setText(text);
        }

        private void update() {
            getStyleClass().removeAll("indetermined", "checked", "unchecked");
            switch (getState()) {
                case UNCHECKED:
                    icon.setGlyphName("SQUARE");
                    getStyleClass().add("unchecked");
                    break;
                case CHECKED:
                    icon.setGlyphName("CHECK_SQUARE");
                    getStyleClass().add("checked");
                    break;
                case INDETERMINED:
                    icon.setGlyphName("MINUS_SQUARE");
                    getStyleClass().add("indetermined");
                    break;
            }
        }

        private void handleClick(Event event) {
            LOGGER.debug("handle click, current state is {}", getState());
            switch (getState()) {
                case INDETERMINED:
                case UNCHECKED:
                    setState(CheckState.CHECKED);
                    break;
                case CHECKED:
                    setState(CheckState.UNCHECKED);
                    break;
            }
        }
    }
}
