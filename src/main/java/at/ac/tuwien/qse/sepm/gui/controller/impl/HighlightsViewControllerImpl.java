package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.control.ImageTile;
import at.ac.tuwien.qse.sepm.gui.control.JourneyPlaceList;
import at.ac.tuwien.qse.sepm.gui.control.WikipediaInfoPane;
import at.ac.tuwien.qse.sepm.gui.controller.HighlightsViewController;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.BufferedBatchOperation;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HighlightsViewControllerImpl implements HighlightsViewController {

    private static final Logger LOGGER = LogManager.getLogger();

    List<ImageTile> tagImageTiles = new ArrayList<>();
    @FXML
    private BorderPane root;
    @FXML
    private HBox wikipediaInfoPaneContainer;
    @FXML
    private ImageTile tag1, tag2, tag3, tag4, tag5, good;
    @FXML
    private GoogleMapScene googleMapScene;
    @FXML
    private JourneyPlaceList journeyPlaceList;
    @FXML
    private GridPane gridPane;

    private FontAwesomeIconView wikipediaPlaceholder = new FontAwesomeIconView(FontAwesomeIcon.INFO);

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private TagService tagService;
    @Autowired
    private WikipediaService wikipediaService;
    private WikipediaInfoPane wikipediaInfoPane;

    // photos for currently selected journey
    private List<Photo> photos = new ArrayList<>();

    @Autowired
    private ScheduledExecutorService scheduler;
    private BufferedBatchOperation<Photo> addBuffer;

    @Autowired
    public void setScheduler(ScheduledExecutorService scheduler) {
        addBuffer = new BufferedBatchOperation<>(this::handlePhotosAdded, scheduler);
    }

    @FXML
    private void initialize() {
        tagImageTiles.addAll(Arrays.asList(tag1, tag2, tag3, tag4, tag5));

        wikipediaInfoPane = new WikipediaInfoPane(wikipediaService);
        HBox.setHgrow(wikipediaInfoPane, Priority.ALWAYS);
        wikipediaInfoPaneContainer.getChildren().addAll(wikipediaInfoPane, wikipediaPlaceholder);

        wikipediaPlaceholder.setGlyphSize(80);
        wikipediaInfoPaneContainer.setAlignment(Pos.CENTER);
        wikipediaPlaceholder.getStyleClass().addAll("wikipedia-placeholder");
        wikipediaInfoPane.managedProperty().bind(wikipediaInfoPane.visibleProperty());
        wikipediaPlaceholder.visibleProperty().bind(wikipediaInfoPane.visibleProperty().not());
        wikipediaInfoPane.setVisible(false);

        journeyPlaceList.setOnJourneySelected(this::handleJourneySelected);
        journeyPlaceList.setOnPlaceSelected(this::handlePlaceSelected);
        journeyPlaceList.setOnAllPlacesSelected(this::handleAllPlacesSelected);

        // give each row and each column in the grid the same size
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(25);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(25);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(25);
        gridPane.getColumnConstraints().addAll(column1, column2, column3, column4);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(33);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(33);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(33);
        gridPane.getRowConstraints().addAll(row1, row2, row3);

        clusterService.subscribeJourneyChanged((journey) ->
                scheduler.schedule(() -> Platform.runLater(this::reloadJourneys), 2, TimeUnit.SECONDS)
        );

        clusterService.subscribePlaceChanged((place) ->
                scheduler.schedule(() -> Platform.runLater(this::reloadJourneys), 2, TimeUnit.SECONDS)
        );

        photoService.subscribeCreate(addBuffer::add);

        reloadJourneys();
    }

    private void reloadJourneys() {
        try {
            for (Journey journey : clusterService.getAllJourneys()) {
                List<Place> places = clusterService.getPlacesByJourneyChronological(journey);
                journeyPlaceList.addJourney(journey, places);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden der Reisen", "");
        }
    }

    private void handlePhotosAdded(List<Photo> photos) {
        Platform.runLater(() -> {
            Journey journey = journeyPlaceList.getSelectedJourney();

            if (journey == null) {
                return;
            }

            // add photos which belong to the current journey to the list
            this.photos.addAll(photos.stream().filter(p -> journey.equals(p.getData().getJourney()))
                    .collect(Collectors.toList()));

            // reload the photos in the grid view
            setGoodPhotos(journeyPlaceList.getSelectedPlace());
            setMostUsedTagsWithPhotos(journeyPlaceList.getSelectedPlace());
        });
    }

    private void handleJourneySelected(Journey journey) {
        // load photos for the selected journey

        try {
            photos = photoService.getAllPhotos().stream()
                    .filter(p -> journey.equals(p.getData().getJourney()))
                    .collect(Collectors.toList());
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden der Fotos", "");
            return;
        }

        handlePlaceSelected(null);
    }

    private void handlePlaceSelected(Place place) {
        LOGGER.debug("selected place {}", place);

        List<Place> places = journeyPlaceList.getPlacesForJourney(journeyPlaceList.getSelectedJourney());

        if (place == null) {
            // draw journey for all places
            wikipediaInfoPane.setVisible(false);
            drawJourney(places, true);
        } else {
            List<Place> placesUntil = places.subList(0, places.indexOf(place) + 1);
            // draw journey until place
            drawJourney(placesUntil, false);

            wikipediaInfoPane.showDefaultWikiInfo(place);
            wikipediaInfoPane.setVisible(true);
        }

        setGoodPhotos(place);
        setMostUsedTagsWithPhotos(place);
    }

    private void handleAllPlacesSelected(Journey journey) {
        LOGGER.debug("All places selected");

        handlePlaceSelected(null);
    }

    /**
     * Sets the good rated photos for the heart imagetile based on a filter.
     *
     * @param place
     */
    private void setGoodPhotos(Place place) {

        PhotoFilter filter;
        if (place == null) {
            filter = new GoodPhotoFilter();
        } else {
            filter = new GoodPhotoForPlaceFilter(place);
        }

        List<Photo> goodPhotos = photos.stream()
                .filter(filter)
                .collect(Collectors.toList());

        good.clearImageTile();
        good.setPhotos(goodPhotos);
        good.setGood();
    }

    private void setMostUsedTagsWithPhotos(Place place) {
        //TODO we should use our own photofilter.
        List<Photo> filteredByPlace;

        if (place == null) {
            filteredByPlace = photos;
        } else {
            filteredByPlace = photos.stream()
                    .filter(p -> p.getData().getPlace().getId().equals(place.getId()))
                    .collect(Collectors.toList());
        }
        try {
            List<Tag> mostUsedTags = tagService.getMostFrequentTags(filteredByPlace);

            for (int i = 0; i < mostUsedTags.size(); i++) {
                Tag t = mostUsedTags.get(i);
                List<Photo> filteredByTags = filteredByPlace.stream().filter(p -> p.getData().getTags().contains(t)).collect(Collectors.toList());
                ImageTile tagImageTile = tagImageTiles.get(i);
                tagImageTile.clearImageTile();
                tagImageTile.setPhotos(filteredByTags);
                tagImageTile.setTag(t);
            }
            int remainingEmptyTagImageTiles = 5 - mostUsedTags.size();
            fillWithPhotos(remainingEmptyTagImageTiles, filteredByPlace);

        } catch (ServiceException e) {
            fillWithPhotos(5, filteredByPlace);
        }

    }

    private void fillWithPhotos(int nrOfPhotos, List<Photo> filteredByPlace) {
        int i = 0;
        while (nrOfPhotos > 0) {
            ImageTile tagImageTile = tagImageTiles.get(5 - nrOfPhotos);
            tagImageTile.clearImageTile();
            if (i < filteredByPlace.size()) {
                tagImageTile.setPhotos(filteredByPlace.subList(i, i + 1));
            }
            nrOfPhotos--;
            i++;
        }
    }

    private void drawJourney(List<Place> places, boolean allPlaces) {
        googleMapScene.clear();

        if (places.isEmpty()) {
            return;
        }

        List<LatLong> path = places.stream()
                .map(p -> new LatLong(p.getLatitude(), p.getLongitude()))
                .collect(Collectors.toList());

        googleMapScene.drawPolyline(path);

        if (allPlaces) {
            googleMapScene.fitToMarkers();
        } else {
            googleMapScene.fitToLastTwoMarkers();
            LatLong position = new LatLong(places.get(places.size()-1).getLatitude(), places.get(places.size()-1).getLongitude());
            googleMapScene.addMarker(position);
        }
    }

    private class GoodPhotoFilter extends PhotoFilter {

        public GoodPhotoFilter() {
            getRatingFilter().getIncluded().add(Rating.GOOD);
        }

        @Override
        public boolean test(Photo photo) {
            return photo.getData().getRating() == Rating.GOOD;
        }
    }

    private class GoodPhotoForPlaceFilter extends GoodPhotoFilter {
        private final Place place;

        public GoodPhotoForPlaceFilter(Place place) {
            super();
            this.place = place;
        }

        @Override
        public boolean test(Photo photo) {
            return super.test(photo) && photo.getData().getPlace().equals(place);
        }
    }
}
