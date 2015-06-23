package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.*;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.JourneyFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class HighlightsViewController {

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

    @FXML
    private void initialize() {
        tagImageTiles.addAll(Arrays.asList(tag1, tag2, tag3, tag4, tag5));

        wikipediaInfoPane = new WikipediaInfoPane(wikipediaService);
        wikipediaInfoPaneContainer.getChildren().add(wikipediaInfoPane);

        journeyPlaceList.setOnJourneySelected(this::handleJourneySelected);
        journeyPlaceList.setOnPlaceSelected(this::handlePlaceSelected);

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

        reloadJourneys();
    }

    public void reloadJourneys() {

        try {
            for (Journey journey : clusterService.getAllJourneys()) {
                List<Place> places = clusterService.getPlacesByJourneyChronological(journey);
                journeyPlaceList.addJourney(journey, places);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden der Reisen", "");
        }
    }

    private void handleJourneySelected(Journey journey) {
        // load photos for the selected journey
        try {
            JourneyFilter filter = new JourneyFilter();
            filter.getIncludedJourneys().add(journey);

            photos = photoService.getAllPhotos().stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden der Fotos", "");
            return;
        }

        // draw the journey on the map
        List<Place> places = journeyPlaceList.getPlacesForJourney(journey);
        googleMapScene.clear();
        handlePlaceSelected(null);
    }

    private void handlePlaceSelected(Place place) {
        List<Place> places = journeyPlaceList.getPlacesForJourney(journeyPlaceList.getSelectedJourney());
        googleMapScene.clear();

        if(place==null){
            // draw journey for all places
            drawJourney(places, true);
        }
        else{
            List<Place> placesUntil = places.subList(0, places.indexOf(place) + 1);
            // draw journey until place
            drawJourney(placesUntil, false);
            wikipediaInfoPane.showDefaultWikiInfo(place);
        }

        setGoodPhotos(place);
        setMostUsedTagsWithPhotos(place);
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
            if(i < filteredByPlace.size()){
                tagImageTile.setPhotos(filteredByPlace.subList(i,i+1));
            }
            nrOfPhotos--;
            i++;
        }
    }

    public void drawJourney(List<Place> places, boolean allPlaces) {
        googleMapScene.clear();

        if (places.isEmpty()) {
            return;
        }

        List<LatLong> path = places.stream()
                .map(p -> new LatLong(p.getLatitude(), p.getLongitude()))
                .collect(Collectors.toList());

        googleMapScene.drawPolyline(path);

        if(allPlaces){
            googleMapScene.fitToMarkers();
        }
        else{
            googleMapScene.fitToLastTwoMarkers();
            LatLong position = new LatLong(places.get(places.size()-1).getLatitude(), places.get(places.size()-1).getLongitude());
            googleMapScene.addMarker(position);
        }
    }

    private class GoodPhotoFilter extends PhotoFilter {

        public GoodPhotoFilter() {
            getIncludedRatings().add(Rating.GOOD);
        }

        @Override
        public boolean test(Photo photo) {
            // TODO: don't override
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
            // TODO: don't override
            return super.test(photo) && photo.getData().getPlace().equals(place) ;
        }
    }
}
