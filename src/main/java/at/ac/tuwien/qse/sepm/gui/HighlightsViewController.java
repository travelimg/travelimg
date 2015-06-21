package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.WikipediaInfoPane;
import at.ac.tuwien.qse.sepm.gui.grid.ImageGrid;
import at.ac.tuwien.qse.sepm.gui.util.GeoUtils;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.JourneyFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
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
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HighlightsViewController {

    private static final Logger LOGGER = LogManager.getLogger();
    List<Button> tagButtons = new ArrayList<>();
    @FXML
    private BorderPane root, left, FotoContainer, treeBoarder, timeLine;
    @FXML
    private GoogleMapsScene mapsScene;
    @FXML
    private VBox journeys, tree, tagheartContainer;
    @FXML
    private HBox titleHBox, tagContainer, wikipediaInfoPaneContainer, mapContainer, firstFourTagsHBox;
    @FXML
    private ScrollPane scrollPhotoView, treeScroll;
    @FXML
    private Label titleLabel;
    @FXML
    private Button tag1, tag2, tag3, tag4, tag5, good;
    @FXML
    private StrokeLineCap lineCap;
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
    private HashMap<PlaceDate, List<Photo>> orderedPlacesAndPhotos = new HashMap<>();
    private List<Photo> currentPhotosOfSelectedJourney = new ArrayList<>();
    private List<Photo> goodPhotosList = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private List<Button> buttonAr = new LinkedList<>();
    private Label noJourneysAvailableLabel = new Label("Keine Reisen gefunden. Bitte fügen Sie eine neue ein.");
    private WikipediaInfoPane wikipediaInfoPane;
    private GoogleMapView mapView;
    private GoogleMap googleMap;
    private Marker actualMarker;
    private ImageCache imageCache;
    private Line redLine;

    @Autowired
    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void setMap(GoogleMapsScene map) {
        this.mapsScene = map;
        mapView = map.getMapView();
        mapView.addMapInializedListener(new MapComponentInitializedListener() {
            @Override
            public void mapInitialized() {
                //wait for the map to initialize.
                googleMap = mapView.getMap();
            }
        });
        mapContainer.getChildren().add(map.getMapView());
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

        journeysListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                handleJourneySelected(journeysListView.getSelectionModel().getSelectedItem());
            }
        });
        noJourneysAvailableLabel.setWrapText(true);
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

            currentPhotosOfSelectedJourney = photoService.getAllPhotos()
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList());
            /*
                CLEAR THE HASHMAPS
             */
            orderedPlacesAndPhotos.clear();
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
            Map<Place, List<Photo>> photosByPlace = getPhotosByPlace(places);


            // merge Place with DataTime
            HashMap<LocalDateTime, Place> orderedPlaces = new HashMap<>();

            for (Place ple : photosByPlace.keySet()) {
                LocalDateTime min = LocalDateTime.MAX;
                for (Photo p : photosByPlace.get(ple)) {

                    if (p.getData().getDatetime().compareTo(min) < 0) {
                        min = p.getData().getDatetime();
                    }
                }
                orderedPlaces.put(min, ple);
            }

            // order the LocalDateTime
            List<LocalDateTime> sortedKeys = new ArrayList<>(orderedPlaces.keySet());
            Collections.reverse(sortedKeys);


            // final HashMap with DateTime, Place and photos
            for (LocalDateTime l : sortedKeys) {
                PlaceDate pl = new PlaceDate(orderedPlaces.get(l), l);
                orderedPlacesAndPhotos.put(pl, photosByPlace.get(orderedPlaces.get(l)));
            }


            ArrayList<Place> orderedPlacesList = new ArrayList<>();
            for (PlaceDate pd : orderedPlacesAndPhotos.keySet()) {
                orderedPlacesList.add(pd.getPlace());
            }

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
                    clearMap();
                    drawDestinationsAsPolyline(GeoUtils.toLatLong(new ArrayList<Place>(places)));
                    setGoodPhotos(null);
                    setMostUsedTagsWithPhotos(null);
                }
            });
            v.getChildren().add(rbAll);
            rbAll.setSelected(true);
            clearMap();
            drawDestinationsAsPolyline(GeoUtils.toLatLong(orderedPlacesList));
            setGoodPhotos(null);
            setMostUsedTagsWithPhotos(null);
            //reloadImages();

            int pos = 0;
            for (PlaceDate pd : orderedPlacesAndPhotos.keySet()) {
                Place p = pd.getPlace();
                RadioButton rb = new RadioButton(p.getCity());
                rb.setToggleGroup(group);
                final int finalPos = pos;
                rb.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        handlePlaceSelected(orderedPlacesList, p, finalPos);
                    }
                });
                v.getChildren().add(rb);
                pos++;
            }
            left.setCenter(v);


        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private Map<Place, List<Photo>> getPhotosByPlace(Set<Place> places) {
        List<Photo> photos;
        Map<Place, List<Photo>> photosByPlace = new HashMap<>();

        try {
            photos = photoService.getAllPhotos();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Konnte nicht alle Fotos laden", "");
            return photosByPlace;
        }

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
        if (goodPhotosList.size() != 0) {
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
        List<Photo> filteredByPlace = new ArrayList<>();

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

    private void drawDestinationsAsPolyline(LatLong[] path) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.path(new MVCArray(path))
                .clickable(false)
                .draggable(false)
                .editable(false)
                .strokeColor("#ff4500")
                .strokeWeight(2)
                .visible(true);
        Polyline polyline = new Polyline(polylineOptions);
        googleMap.addMapShape(polyline);
        polylines.add(polyline);
        GeoUtils.fitMarkersToScreen(path, 0, path.length - 1, mapContainer.getHeight(), mapContainer.getWidth(), mapsScene);

        for (int i = 0; i < path.length; i++) {
            Marker m = new Marker(new MarkerOptions().position(path[i]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
            googleMap.addMarker(m);
            markers.add(m);
        }
    }

    private void drawJourneyUntil(List<Place> places, int pos) {
        clearMap();
        LatLong[] path = GeoUtils.toLatLong(places);
        if (pos == 0) {
            Marker m = new Marker(new MarkerOptions().position(path[pos]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
            googleMap.addMarker(m);
            markers.add(m);
            GeoUtils.fitMarkersToScreen(path, pos, pos, mapContainer.getHeight(), mapContainer.getWidth(), mapsScene);
        } else {
            PolylineOptions polylineOptions = new PolylineOptions();
            MVCArray mvcArray = new MVCArray();
            for (int i = 0; i <= pos; i++) {
                mvcArray.push(path[i]);
                Marker m = new Marker(new MarkerOptions().position(path[i]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
                googleMap.addMarker(m);
                markers.add(m);
            }
            polylineOptions.path(mvcArray)
                    .clickable(false)
                    .draggable(false)
                    .editable(false)
                    .strokeColor("#ff4500")
                    .strokeWeight(2)
                    .visible(true);
            Polyline polyline = new Polyline(polylineOptions);
            googleMap.addMapShape(polyline);
            polylines.add(polyline);
            GeoUtils.fitMarkersToScreen(path, pos - 1, pos, mapContainer.getHeight(),
                    mapContainer.getWidth(), mapsScene);
        }
        actualMarker = new Marker(new MarkerOptions().position(path[pos]));
        googleMap.addMarker(actualMarker);
    }

    private void clearMap() {
        markers.forEach(marker -> googleMap.removeMarker(marker));
        polylines.forEach(line -> googleMap.removeMapShape(line));

        markers.clear();
        polylines.clear();

        if (actualMarker != null) {
            googleMap.removeMarker(actualMarker);
        }
    }

    /**
     * private Class connecting Place with LocalDateTime
     */
    private class PlaceDate {
        private Place place;
        private LocalDateTime date;

        public PlaceDate(Place p, LocalDateTime l) {
            this.place = p;
            this.date = l;
        }

        public Place getPlace() {
            return this.place;
        }

        public void setPlace(Place p) {
            this.place = p;
        }

        public LocalDateTime getDate() {
            return this.date;
        }

        public void setDate(LocalDateTime l) {
            this.date = l;
        }
    }
}
