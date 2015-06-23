package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
    private Node noJourneyPlaceholder;

    private ObjectProperty<Journey> selectedJourney = new SimpleObjectProperty<>(null);
    private ObjectProperty<Place> selectedPlace = new SimpleObjectProperty<>(null);

    private Map<Journey, List<Place>> placesByJourney = new HashMap<>();

    private Consumer<Journey> journeySelectedCallback = null;
    private Consumer<Place> placeSelectedCallback = null;
    private Consumer<Journey> allPlacesSelectedCallback = null;

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
        places.getChildren().clear();

        places.getChildren().add(new AllPlacesEntry());
        places.getChildren().addAll(placesByJourney.get(selectedJourney.get()).stream()
                        .map(PlaceEntry::new)
                        .collect(Collectors.toList())
        );
    }

    private class JourneyEntry extends Button {
        public JourneyEntry(Journey journey) {
            super(journey.getName());

            setOnAction((event) -> selectedJourney.set(journey));
        }
    }

    private class AllPlacesEntry extends Button {
        public AllPlacesEntry() {
            super("Alle Orte");

            setOnAction((event) -> allPlacesSelectedCallback.accept(selectedJourney.get()));
        }
    }

    private class PlaceEntry extends Button {
        public PlaceEntry(Place place) {
            super(place.getCity());

            setOnAction((event) -> selectedPlace.set(place));
        }
    }
}
