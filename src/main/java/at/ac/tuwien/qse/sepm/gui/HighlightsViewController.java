package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.JourneyPlaceList;
import at.ac.tuwien.qse.sepm.gui.control.TravelRouteMap;
import at.ac.tuwien.qse.sepm.gui.control.WikipediaInfoPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.JourneyFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HighlightsViewController {

    private static final Logger LOGGER = LogManager.getLogger();
    List<Button> tagButtons = new ArrayList<>();
    @FXML
    private BorderPane root;
    @FXML
    private HBox wikipediaInfoPaneContainer;
    @FXML
    private Button tag1, tag2, tag3, tag4, tag5, good;
    @FXML
    private TravelRouteMap travelRouteMap;

    @FXML
    private JourneyPlaceList journeyPlaceList;

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private TagService tagService;
    @Autowired
    private WikipediaService wikipediaService;
    private List<Button> buttonAr = new LinkedList<>();
    private WikipediaInfoPane wikipediaInfoPane;
    private ImageCache imageCache;

    // photos for currently selected journey
    private List<Photo> photos = new ArrayList<>();


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

        wikipediaInfoPane = new WikipediaInfoPane(wikipediaService);
        wikipediaInfoPaneContainer.getChildren().add(wikipediaInfoPane);

        journeyPlaceList.setOnJourneySelected(this::handleJourneySelected);
        journeyPlaceList.setOnPlaceSelected(this::handlePlaceSelected);

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
        travelRouteMap.clear();
        travelRouteMap.drawJourney(places);

        // TODO: show tags and good photos for entire journey
    }

    private void handlePlaceSelected(Place place) {
        wikipediaInfoPane.showDefaultWikiInfo(place);

        // draw journey until given place
        List<Place> places = journeyPlaceList.getPlacesForJourney(journeyPlaceList.getSelectedJourney());
        List<Place> placesUntil = places.subList(0, places.indexOf(place) + 1);

        travelRouteMap.clear();
        travelRouteMap.drawJourney(placesUntil);

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

    @FXML
    private void bt_heartPress() {
        List<Photo> goodPhotos = photos.stream()
                .filter(new GoodPhotoFilter())
                .collect(Collectors.toList());

        if (!goodPhotos.isEmpty()) {
            FullscreenWindow fw = new FullscreenWindow(this.imageCache);
            fw.present(goodPhotos, goodPhotos.get(0));
        }
    }

    private Stream<Photo> getGoodPhotos() {
        // todo use filter
        return photos.stream()
                .filter(p -> p.getData().getRating() == Rating.GOOD);
    }



    /**
     * Sets the good rated photos for the heart button based on a filter.
     *
     * @param place
     */
    private void setGoodPhotos(Place place) {
        good.setText("");

        PhotoFilter filter;
        if (place == null) {
            filter = new GoodPhotoFilter();
        } else {
            filter = new GoodPhotoForPlaceFilter(place);
        }

        List<Photo> goodPhotos = photos.stream()
                    .filter(filter)
                    .collect(Collectors.toList());

        if (goodPhotos.isEmpty()) {
            good.setStyle("-fx-background-image: none;");
        } else {
            setBackroundImageForButton(goodPhotos.get(0).getPath(), good);
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
