package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JourneyPlaceList extends VBox {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private VBox root;
    @FXML
    private Button backButton;
    @FXML
    private Label journeyLabel;
    @FXML
    private VBox journeys;
    @FXML
    private VBox places;
    @FXML
    private VBox timeline;
    @FXML
    private Node noJourneyPlaceholder;
    @FXML
    private RadioButton allPlacesButton;

    private ObjectProperty<Journey> selectedJourney = new SimpleObjectProperty<>(null);
    private ObjectProperty<Place> selectedPlace = new SimpleObjectProperty<>(null);

    private Map<Journey, List<Place>> placesByJourney = new HashMap<>();

    private Consumer<Journey> journeySelectedCallback = null;
    private Consumer<Place> placeSelectedCallback = null;
    private Consumer<Journey> allPlacesSelectedCallback = null;
    private ToggleGroup toggleGroup = new ToggleGroup();

    public JourneyPlaceList() {
        FXMLLoadHelper.load(this, this, JourneyPlaceList.class, "view/control/JourneyPlaceList.fxml");
    }

    @FXML
    private void initialize() {
        journeys.managedProperty().bind(journeys.visibleProperty());
        places.managedProperty().bind(places.visibleProperty());
        noJourneyPlaceholder.managedProperty().bind(noJourneyPlaceholder.visibleProperty());

        journeys.visibleProperty().bind(selectedJourney.isNull());
        places.visibleProperty().bind(selectedJourney.isNotNull());
        backButton.visibleProperty().bind(selectedJourney.isNotNull());

        backButton.setOnAction((event) -> selectedJourney.set(null));

        allPlacesButton.setToggleGroup(toggleGroup);
        allPlacesButton.setOnAction((event) -> allPlacesSelectedCallback.accept(selectedJourney.get()));

        selectedJourney.addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                journeyLabel.setText("Reisen");
            } else {
                journeyLabel.setText(newValue.getName());
            }
        }));

        selectedJourney.addListener(((observable, oldValue, newValue) -> {
            LOGGER.debug("Selected journey changed to {}", newValue);
            if (newValue != null) {
                populatePlacesBox();
                journeySelectedCallback.accept(newValue);
            }
        }));

        selectedPlace.addListener((observable, oldValue, newValue) -> {
            LOGGER.debug("Selected place changed to {}", newValue);
            if (newValue != null) {
                placeSelectedCallback.accept(newValue);
            }
        });
    }

    public void setOnJourneySelected(Consumer<Journey> callback) {
        this.journeySelectedCallback = callback;
    }

    public void setOnPlaceSelected(Consumer<Place> callback) {
        this.placeSelectedCallback = callback;
    }

    public void setOnAllPlacesSelected(Consumer<Journey> callback) {
        this.allPlacesSelectedCallback = callback;
    }

    public Journey getSelectedJourney() {
        return selectedJourney.get();
    }

    public List<Place> getPlacesForJourney(Journey journey) {
        return placesByJourney.get(journey);
    }

    public void addJourney(Journey journey, List<Place> places) {
        placesByJourney.put(journey, places);

        update();
    }

    private void update() {
        boolean hasJourney = placesByJourney.size() > 0;

        noJourneyPlaceholder.setVisible(!hasJourney);

        populateJourneyBox();
    }

    private void populateJourneyBox() {
        journeys.getChildren().clear();

        journeys.getChildren().addAll(placesByJourney.keySet().stream()
                        .sorted((j1, j2) -> j1.getStartDate().compareTo(j2.getStartDate()))
                        .map(JourneyEntry::new)
                        .collect(Collectors.toList())
        );
    }

    private void populatePlacesBox() {
        List<Place> placesOfJourney = placesByJourney.get(selectedJourney.get());

        timeline.getChildren().clear();
        PlaceEntry prev = null;

        for (int i = 0; i < placesOfJourney.size(); i++) {
            Place place = placesOfJourney.get(i);
            PlaceEntry current = new PlaceEntry(place);

            current.setPrev(prev);
            if (prev != null) {
                prev.setNext(current);
            }

            timeline.getChildren().add(current);

            if (i != placesOfJourney.size() - 1) {
                Rectangle rect = new Rectangle(5, 40);
                rect.getStyleClass().add("rect");

                VBox vbox = new VBox();
                vbox.setPadding(new Insets(0, 0, 0, 7.0));
                vbox.getChildren().add(rect);
                timeline.getChildren().add(vbox);
            }

            prev = current;
        }

        allPlacesButton.setSelected(true);
    }

    private class JourneyEntry extends Button {
        public JourneyEntry(Journey journey) {
            super(journey.getName());

            setOnAction((event) -> selectedJourney.set(journey));
        }
    }

    private class PlaceEntry extends RadioButton {
        private Place place;
        private PlaceEntry prev = null;
        private PlaceEntry next = null;

        public PlaceEntry(Place place) {
            super(place.getCity());
            this.place = place;

            getStyleClass().add("place-radio-button");

            setToggleGroup(toggleGroup);
            setOnAction(this::handleSelected);
        }

        public void setPrev(PlaceEntry prev) {
            this.prev = prev;
        }

        public void setNext(PlaceEntry next) {
            this.next = next;
        }

        public void setVisited() {
            getStyleClass().removeAll("unvisited");

            if (!getStyleClass().contains("visited")) {
                getStyleClass().add("visited");
            }

            if (prev != null) {
                prev.setVisited();
            }
        }

        public void setUnvisited() {
            getStyleClass().removeAll("visited");

            if (!getStyleClass().contains("unvisited")) {
                getStyleClass().add("unvisited");
            }

            if (next != null) {
                next.setUnvisited();
            }
        }

        private void handleSelected(Event event) {
            selectedPlace.set(this.place);

            setVisited();

            if (next != null) {
                next.setUnvisited();
            }
        }
    }
}
