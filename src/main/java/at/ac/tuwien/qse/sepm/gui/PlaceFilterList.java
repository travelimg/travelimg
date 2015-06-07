package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Place;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// quick hack. unify with FilterList
public class PlaceFilterList extends VBox {

    private final CheckItem header = new CheckItem(null);
    private final Map<Place, CheckItem> items = new IdentityHashMap<>();
    private final Map<String, CountryCheckItem> countryItems = new IdentityHashMap<>();

    private Consumer<List<Place>> changeHandler;

    public PlaceFilterList() {
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

    public List<Place> getValues() {
        return new ArrayList<>(items.keySet());
    }

    public void setValues(List<Place> values) {
        HashMap<String, List<Place>> citiesByCountry = new HashMap<>();

        for (Place city : values) {
            String country = city.getCountry();
            if(citiesByCountry.containsKey(country)) {
                citiesByCountry.get(country).add(city);
            } else {
                ArrayList<Place> cities = new ArrayList<>();
                cities.add(city);
                citiesByCountry.put(country, cities);
            }
        }

        items.clear();
        getChildren().clear();
        getChildren().add(header);

        for (String country : citiesByCountry.keySet()) {
            CountryCheckItem item = new CountryCheckItem(country, citiesByCountry.get(country));
            getChildren().add(item);
            setVgrow(item, Priority.ALWAYS);

            countryItems.put(country, item);

            item.stateProperty().addListener(observable -> onChange());
            for (CheckItem placeItem : item.getPlaceItems()) {
                placeItem.stateProperty().addListener(observable -> onChange());
                this.items.put(placeItem.getPlace(), placeItem);
            }
        }
    }

    public List<Place> getChecked() {
        return items.entrySet().stream()
                .filter(entry -> entry.getValue().getState() == CheckState.CHECKED)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    public void check(Place value) {
        if (!items.containsKey(value)) return;
        items.get(value).setState(CheckState.CHECKED);
    }

    public void uncheck(Place value) {
        if (!items.containsKey(value)) return;
        items.get(value).setState(CheckState.UNCHECKED);
    }

    public void checkAll() {
        items.keySet().forEach(this::check);

        countryItems.values().forEach(item -> item.setState(CheckState.CHECKED));
    }

    public void uncheckAll() {
        items.keySet().forEach(this::uncheck);
        countryItems.values().forEach(item -> item.setState(CheckState.UNCHECKED));
    }

    public void setChangeHandler(Consumer<List<Place>> changeHandler) {
        this.changeHandler = changeHandler;
    }

    private void onChange() {
        if (changeHandler != null) {
            System.out.println("checked: " + getChecked());
            for (Map.Entry<Place, CheckItem> entry : items.entrySet()) {
                System.out.println("" + entry.getKey() + " is " + (entry.getValue().getState() == CheckState.CHECKED));
            }
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

        private final Place place;
        private final FontAwesomeIconView icon = new FontAwesomeIconView();
        private final Label label = new Label();

        public CheckItem(Place place) {
            this.place = place;

            if (place != null) { // not header
                setText(place.getCity());
                getStyleClass().add("city");

                // bad hack
                Node indentation = new Label("  ");
                indentation.setVisible(false);
                getChildren().add(indentation);
            }

            getStyleClass().add("check-item");
            getChildren().addAll(icon, label);

            setAlignment(Pos.CENTER_LEFT);

            setOnMouseClicked(this::handleClick);
            update();
        }

        public Place getPlace() {
            return place;
        }

        public ObjectProperty<CheckState> stateProperty() {
            return stateProperty;
        }

        private final ObjectProperty<CheckState> stateProperty =
                new SimpleObjectProperty<>(this, "state", CheckState.UNCHECKED);

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

    private static class CountryCheckItem extends VBox {

        private static final Logger LOGGER = LogManager.getLogger();

        private final FontAwesomeIconView icon = new FontAwesomeIconView();
        private final Label label = new Label();

        private List<Place> places = new ArrayList<>();
        private List<CheckItem> items = new ArrayList<>();

        public CountryCheckItem(String country, List<Place> cities) {
            this.places = cities;

            HBox box = new HBox();
            getStyleClass().add("check-item");
            box.getChildren().addAll(icon, label);

            setText(country);

            getChildren().add(box);

            for (Place city : cities) {
                if (city.getId() == 1) {
                    items.add(new CheckItem(city));
                    continue;
                }

                CheckItem item = new CheckItem(city);
                getChildren().add(item);

                items.add(item);
            }
            //setAlignment(Pos.CENTER_LEFT);

            setOnMouseClicked(this::handleClick);
            update();
        }

        public List<CheckItem> getPlaceItems() {
            return items;
        }

        public ObjectProperty<CheckState> stateProperty() {
            return stateProperty;
        }

        private final ObjectProperty<CheckState> stateProperty =
                new SimpleObjectProperty<>(this, "state", CheckState.UNCHECKED);

        public CheckState getState() {
            return stateProperty().get();
        }

        public void setState(CheckState state) {
            stateProperty().set(state);

            for (CheckItem item : items) {
                item.setState(state);
            }

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
