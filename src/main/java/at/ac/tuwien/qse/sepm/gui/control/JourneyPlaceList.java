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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;
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
        allPlacesButton.setOnAction((event) -> selectedPlace.set(null));

        selectedJourney.addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                journeyLabel.setText("Verfügbare Reisen");
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
            } else {
                allPlacesSelectedCallback.accept(getSelectedJourney());
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

    public Place getSelectedPlace() { return selectedPlace.get(); }

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

        for (int i = 0; i < placesOfJourney.size(); i++) {
            Place place = placesOfJourney.get(i);
            PlaceEntry current = new PlaceEntry(place);

            timeline.getChildren().add(current);

            if (i != placesOfJourney.size() - 1) {
                Rectangle rect = new Rectangle(5, 40);
                rect.getStyleClass().add("rect");

                VBox vbox = new VBox();
                vbox.setPadding(new Insets(0, 0, 0, 7.0));
                vbox.getChildren().add(rect);
                timeline.getChildren().add(vbox);
            }
        }

        allPlacesButton.setSelected(true);
    }

    private class JourneyEntry extends HBox {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.yyyy");

        public JourneyEntry(Journey journey) {
            super();
            getStyleClass().add("journey-entry");

            String time = String.format("(%s - %s)", formatter.format(journey.getStartDate()), formatter.format(journey.getEndDate()));

            Label name = new Label(journey.getName());
            Label duration = new Label(time);

            name.getStyleClass().add("name");
            duration.getStyleClass().add("duration");

            getChildren().addAll(name, duration);

            setOnMouseClicked((event) -> selectedJourney.set(journey));
        }
    }

    private class PlaceEntry extends RadioButton {

        public PlaceEntry(Place place) {
            super(place.getCity());
            getStyleClass().add("place-radio-button");

            setToggleGroup(toggleGroup);
            selectedProperty().addListener(observable -> {
                if (isSelected()) {
                    selectedPlace.set(place);
                }
            });
        }
    }
}
