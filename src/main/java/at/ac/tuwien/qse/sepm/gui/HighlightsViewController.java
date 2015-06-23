package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.TravelRouteMap;
import at.ac.tuwien.qse.sepm.gui.control.WikipediaInfoPane;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.JourneyFilter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class HighlightsViewController {

    private static final Logger LOGGER = LogManager.getLogger();
    List<Button> tagButtons = new ArrayList<>();
    @FXML
    private BorderPane root, left, FotoContainer, treeBoarder, timeLine;
    @FXML
    private VBox journeys, tree, tagheartContainer;
    @FXML
    private HBox titleHBox, tagContainer, wikipediaInfoPaneContainer, firstFourTagsHBox;
    @FXML
    private ScrollPane scrollPhotoView, treeScroll;
    @FXML
    private Label titleLabel;
    @FXML
    private Button tag1, tag2, tag3, tag4, tag5, good;
    @FXML
    private StrokeLineCap lineCap;
    @FXML
    private TravelRouteMap travelRouteMap;

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private TagService tagService;
    @Autowired
    private WikipediaService wikipediaService;
    private ListView<Journey> journeysListView = new ListView<>();
    private HashMap<Place, List<Tag>> placesAndTags = new HashMap<>();
    private List<Photo> currentPhotosOfSelectedJourney = new ArrayList<>();
    private List<Photo> goodPhotosList = new ArrayList<>();
    private List<Button> buttonAr = new LinkedList<>();
    private Label noJourneysAvailableLabel = new Label("Keine Reisen gefunden. Bitte fügen Sie eine neue ein.");
    private WikipediaInfoPane wikipediaInfoPane;
    private ImageCache imageCache;
    private Line redLine;


    @Autowired
    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }


    @FXML
    private void initialize() {
        buttonAr.add(tag1);
        buttonAr.add(tag2);
        buttonAr.add(tag3);
        buttonAr.add(tag4);
        tagButtons.addAll(Arrays.asList(tag1, tag2, tag3, tag4, tag5));
        redLine = new Line(100, 50, 100, 500);
        redLine.setStroke(Color.DARKGRAY);
        redLine.setStrokeWidth(2);
        redLine.setStrokeLineCap(StrokeLineCap.ROUND);

        redLine.getStrokeDashArray().addAll(2D, 21D);
        redLine.setStrokeDashOffset(30);

        wikipediaInfoPane = new WikipediaInfoPane(wikipediaService);
        wikipediaInfoPaneContainer.getChildren().add(wikipediaInfoPane);

        journeysListView.setCellFactory(new Callback<ListView<Journey>, ListCell<Journey>>() {

            public ListCell<Journey> call(ListView<Journey> param) {
                final ListCell<Journey> cell = new ListCell<Journey>() {
                    @Override
                    public void updateItem(Journey item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        }
                    }
                }; // ListCell
                return cell;
            }
        }); // setCellFactory

        journeysListView.setOnMouseClicked(event -> {
            handleJourneySelected(journeysListView.getSelectionModel().getSelectedItem());
        });
        noJourneysAvailableLabel.setWrapText(true);

        reloadJourneys();
    }

    public void reloadJourneys() {
        //photoView.getChildren().clear();
        tree.getChildren().clear();
        Label lab2 = new Label();
        lab2.setText("Bitte eine Reise auswählen");
        //.getChildren().add(lab2);

        journeys.getChildren().clear();
        journeysListView.getItems().clear();
        try {
            List<Journey> listOfJourneys = clusterService.getAllJourneys();
            if (listOfJourneys.size() > 0) {
                journeysListView.getItems().addAll(listOfJourneys);
                journeys.getChildren().add(journeysListView);
            } else {
                journeys.getChildren().add(noJourneysAvailableLabel);
            }
        } catch (ServiceException e) {
            journeys.getChildren().add(noJourneysAvailableLabel);
        }
    }

    private void handleJourneySelected(Journey journey) {

        try {
            JourneyFilter filter = new JourneyFilter();
            filter.getIncludedJourneys().add(journey);

            List<Photo> allPhotos = photoService.getAllPhotos();

            currentPhotosOfSelectedJourney = allPhotos.stream()
                    .filter(filter)
                    .collect(Collectors.toList());
            /*
                CLEAR THE HASHMAPS
             */
            placesAndTags.clear();

            HBox placesTitleHBox = new HBox();
            Label placesTitleLabel = new Label("Orte");
            placesTitleLabel.setStyle("-fx-background-color: #333333; -fx-font-size: 18; -fx-text-fill: white; -fx-padding: 5 0 5 10px;");
            placesTitleLabel.setPrefWidth(259);
            placesTitleLabel.setPrefHeight(20);

            Button back = new Button("<");
            back.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    clearMap();
                    left.setTop(titleHBox);
                    left.setCenter(journeys);
                }
            });

            placesTitleHBox.getChildren().add(back);
            placesTitleHBox.getChildren().add(placesTitleLabel);
            left.setTop(placesTitleHBox);

            Set<Place> places = clusterService.getPlacesByJourney(journey);


            /*
                merge Places with Photos
                output is a HashSet
             */
            Map<Place, List<Photo>> photosByPlace = getPhotosByPlace(places, allPhotos);
            List<Place> orderedPlaces = orderPlacesByVisitingDate(places, photosByPlace);

            VBox v = new VBox();
            v.setSpacing(5.0);
            v.setStyle("-fx-font-size: 16;");
            v.setPadding(new Insets(5.0, 0.0, 0.0, 10.0));
            final ToggleGroup group = new ToggleGroup();
            RadioButton rbAll = new RadioButton("Alle Orte");
            rbAll.setToggleGroup(group);
            rbAll.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    travelRouteMap.clear();
                    travelRouteMap.drawJourney(new ArrayList<>(places));

                    setGoodPhotos(null);
                    setMostUsedTagsWithPhotos(null);
                }
            });
            v.getChildren().add(rbAll);
            rbAll.setSelected(true);

            travelRouteMap.clear();
            travelRouteMap.drawJourney(orderedPlaces);

            setGoodPhotos(null);
            setMostUsedTagsWithPhotos(null);
            //reloadImages();

            for (Place place : orderedPlaces) {
                RadioButton button = new RadioButton(place.getCity());
                button.setToggleGroup(group);
                button.setOnAction((event) -> {
                    handlePlaceSelected(orderedPlaces, place, orderedPlaces.indexOf(place));
                });

                v.getChildren().add(button);
            }
            left.setCenter(v);


        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private Map<Place, List<Photo>> getPhotosByPlace(Set<Place> places, List<Photo> photos) {
        Map<Place, List<Photo>> photosByPlace = new HashMap<>();

        places.forEach(place -> {
            photosByPlace.put(place, photos.stream()
                .filter(p -> p.getData().getPlace().equals(place))
                .collect(Collectors.toList())
            );
        });

        return photosByPlace;
    }

    private List<Place> orderPlacesByVisitingDate(Set<Place> places, Map<Place, List<Photo>> photosByPlace) {
        List<Place> orderedPlaces = new ArrayList<>(places);

        // sort places by time
        // the photo with the lowest datetime which belongs to a place determines the visiting time
        orderedPlaces.sort((p1, p2) -> {
            List<Photo> l1 = photosByPlace.get(p1);
            List<Photo> l2 = photosByPlace.get(p2);

            Optional<Photo> min1 = l1.stream().min((ph1, ph2) -> ph1.compareTo(ph2));
            Optional<Photo> min2 = l2.stream().min((ph1, ph2) -> ph1.compareTo(ph2));

            if (!min1.isPresent() || !min2.isPresent())
                return 0;

            return min1.get().getData().getDatetime().compareTo(min2.get().getData().getDatetime());
        });

        return orderedPlaces;
    }

    private void handlePlaceSelected(List<Place> places, Place place, int pos) {
        wikipediaInfoPane.showDefaultWikiInfo(place);
        drawJourneyUntil(places, pos);
        setGoodPhotos(place);
        setMostUsedTagsWithPhotos(place);
    }

    @FXML
    private void bt_heartPress() {
        if (!goodPhotosList.isEmpty()) {
            FullscreenWindow fw = new FullscreenWindow(this.imageCache);
            fw.present(goodPhotosList, goodPhotosList.get(0));
        }
    }

    /**
     * Sets the good rated photos for the heart button based on a filter.
     *
     * @param place
     */
    private void setGoodPhotos(Place place) {
        //TODO we should use our own photofilter.
        good.setText("");
        if (place == null) {
            goodPhotosList = currentPhotosOfSelectedJourney.stream()
                    .filter(p -> p.getData().getRating().equals(Rating.GOOD))
                    .collect(Collectors.toList());
        } else {
            goodPhotosList = currentPhotosOfSelectedJourney.stream()
                    .filter(p -> p.getData().getPlace().getId().equals(place.getId())
                            && p.getData().getRating().equals(Rating.GOOD))
                    .collect(Collectors.toList());
        }

        if (goodPhotosList.isEmpty()) {
            good.setStyle("-fx-background-image: none;");
        } else {
            setBackroundImageForButton(goodPhotosList.get(0).getPath(), good);
        }
    }

    private void setMostUsedTagsWithPhotos(Place place) {
        //TODO we should use our own photofilter.
        List<Photo> filteredByPlace;

        tagButtons.forEach(button -> {
            button.setStyle("-fx-background-image: none;");
            button.setText("");
        });

        if (place == null) {
            filteredByPlace = currentPhotosOfSelectedJourney;
        } else {
            filteredByPlace = currentPhotosOfSelectedJourney.stream()
                    .filter(p -> p.getData().getPlace().getId().equals(place.getId()))
                    .collect(Collectors.toList());
        }
        try {
            List<Tag> mostUsedTags = tagService.getMostFrequentTags(filteredByPlace);

            for (int i = 0; i < mostUsedTags.size(); i++) {
                Tag t = mostUsedTags.get(i);
                List<Photo> filteredByTags = filteredByPlace.stream().filter(p -> p.getData().getTags().contains(t)).collect(Collectors.toList());
                Button tagButton = tagButtons.get(i);
                tagButton.setText(t.getName());
                setBackroundImageForButton(filteredByTags.get(0).getPath(), tagButton);
                tagButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (filteredByTags.size() != 0) {
                            FullscreenWindow fw = new FullscreenWindow(imageCache);
                            fw.present(filteredByTags, filteredByTags.get(0));
                        }
                    }
                });

            }
            int remainingEmptyTagButtons = 5 - mostUsedTags.size();
            fillWithPhotos(remainingEmptyTagButtons, filteredByPlace);

        } catch (ServiceException e) {
            fillWithPhotos(5, filteredByPlace);
        }

    }

    private void fillWithPhotos(int nrOfPhotos, List<Photo> filteredByPlace) {
        int i = 0;
        while (nrOfPhotos > 0 && i < filteredByPlace.size()) {
            Button tagButton = tagButtons.get(5 - nrOfPhotos);
            setBackroundImageForButton(filteredByPlace.get(i).getPath(), tagButton);
            nrOfPhotos--;
            i++;
        }
    }

    /**
     * Sets an image as the background of the button
     *
     * @param path   the path to the image
     * @param button
     */
    private void setBackroundImageForButton(String path, Button button) {
        try {
            button.setStyle("-fx-background-image: url('" + Paths.get(path).toUri().toURL().toString() + "');" +
                    "-fx-background-size: 100% 100%;");
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to convert photo path to URL", e);
        }
    }

    private void drawJourneyUntil(List<Place> places, int pos) {
        List<Place> placesUntil = places.subList(0, pos + 1);
        travelRouteMap.drawJourney(placesUntil);
    }

    private void clearMap() {
        if (travelRouteMap != null) {
            travelRouteMap.clear();
        }
    }
}
