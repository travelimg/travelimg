package at.ac.tuwien.qse.sepm.gui.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.FilterList;
import at.ac.tuwien.qse.sepm.gui.Inspector;
import at.ac.tuwien.qse.sepm.gui.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.JourneyDialog;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for organizer view which is used for browsing photos by month.
 */
public class OrganizerImpl implements Organizer {

    private static final Logger LOGGER = LogManager.getLogger(OrganizerImpl.class);
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy MMM");
    @Autowired
    private PhotographerService photographerService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private Inspector inspectorController;
    @Autowired
    private TagService tagService;
    @Autowired
    private PhotoFilter filter;
    @FXML
    private BorderPane root;
    @FXML
    private Button importButton;
    @FXML
    private Button flickrButton;
    @FXML
    private Button presentButton;
    @FXML
    private Button addJourneyButton;
    @FXML
    private VBox filterContainer;
    @FXML
    private FilterList<Rating> ratingListView;
    @FXML
    private FilterList<Tag> categoryListView;
    @FXML
    private FilterList<Photographer> photographerListView;
    @FXML
    private FilterList<Journey> journeyListView;
    @FXML
    private FilterList<Place> placeListView;
    @FXML
    private Button resetButton;
    private Runnable filterChangeCallback;

    @Override public void setPresentAction(Runnable callback) {
        LOGGER.debug("setting present action");
        presentButton.setOnAction(event -> callback.run());
    }

    @Override public void setImportAction(Runnable callback) {
        LOGGER.debug("setting import action");
        importButton.setOnAction(event -> callback.run());
    }

    @Override public void setJourneyAction(Runnable callback) {
        LOGGER.debug("setting journey action");
        addJourneyButton.setOnAction(event -> callback.run());
    }

    @Override public void setFlickrAction(Runnable callback) {
        LOGGER.debug("setting flickr action");
        flickrButton.setOnAction(event -> callback.run());
    }

    @Override public void setFilterChangeAction(Runnable callback) {
        LOGGER.debug("setting filter change action");
        filterChangeCallback = callback;
    }

    @Override public PhotoFilter getFilter() {
        return filter;
    }

    @FXML
    private void initialize() {
        resetButton.setOnAction(event -> resetFilter());

        ratingListView = new FilterList<>(value -> {
            switch (value) {
                case GOOD:
                    return "Gut";
                case NEUTRAL:
                    return "Neutral";
                case BAD:
                    return "Schlecht";
                default:
                    return "Unbewertet";
            }
        });
        ratingListView.setTitle("Bewertungen");
        ratingListView.setChangeHandler(this::handleRatingsChange);
        categoryListView = new FilterList<>(value -> {
            if (value == null) return "Nicht kategorisiert";
            return value.getName();
        });
        categoryListView.setTitle("Kategorien");
        categoryListView.setChangeHandler(this::handleCategoriesChange);
        photographerListView = new FilterList<>(value -> value.getName());
        photographerListView.setTitle("Fotografen");
        photographerListView.setChangeHandler(this::handlePhotographersChange);
        journeyListView = new FilterList<>(value -> {
            if (value == null) return "Keiner Reise zugeordnet";
            return value.getName();
        });
        journeyListView.setTitle("Reisen");
        journeyListView.setChangeHandler(this::handleJourneysChange);
        placeListView = new FilterList<Place>(value -> {
            if (value == null) return "Keinem Ort zugeordnet";
            return value.getCountry() + ", " + value.getCity();
        });
        placeListView.setTitle("Orte");
        placeListView.setChangeHandler(this::handlePlacesChange);

        filterContainer.getChildren().addAll(ratingListView, categoryListView, photographerListView, journeyListView,
                placeListView);


        refreshLists();
        resetFilter();
    }

    private void handleFilterChange() {
        LOGGER.debug("filter changed");
        if (filterChangeCallback == null) return;
        filterChangeCallback.run();
    }

    private void handleRatingsChange(List<Rating> values) {
        LOGGER.debug("rating filter changed");
        filter.getIncludedRatings().clear();
        filter.getIncludedRatings().addAll(values);
        handleFilterChange();
    }

    private void handleCategoriesChange(List<Tag> values) {
        LOGGER.debug("category filter changed");
        filter.setUntaggedIncluded(values.contains(null));
        values.remove(null);
        filter.getIncludedCategories().clear();
        filter.getIncludedCategories().addAll(values);
        handleFilterChange();
    }

    private void handlePhotographersChange(List<Photographer> values) {
        LOGGER.debug("photographer filter changed");
        filter.getIncludedPhotographers().clear();
        filter.getIncludedPhotographers().addAll(values);
        handleFilterChange();
    }

    private void handleJourneysChange(List<Journey> values) {
        LOGGER.debug("journey filter changed");
        filter.getIncludedJourneys().clear();
        filter.getIncludedJourneys().addAll(values);
        handleFilterChange();
    }

    private void handlePlacesChange(List<Place> values) {
        LOGGER.debug("place filter changed");
        filter.getIncludedPlaces().clear();
        filter.getIncludedPlaces().addAll(values);
        handleFilterChange();
    }

    private void refreshLists() {
        LOGGER.debug("refreshing filter");

        ratingListView.setValues(getAllRatings());
        categoryListView.setValues(getAllCategories());
        photographerListView.setValues(getAllPhotographers());
        journeyListView.setValues(getAllJourneys());
        placeListView.setValues(getAllPlaces());
    }

    private List<Rating> getAllRatings() {
        LOGGER.debug("fetching ratings");
        List<Rating> list = new LinkedList<Rating>();
        list.add(Rating.GOOD);
        list.add(Rating.NEUTRAL);
        list.add(Rating.BAD);
        list.add(Rating.NONE);
        LOGGER.debug("fetching ratings succeeded with {} items", list.size());
        return list;
    }

    private List<Tag> getAllCategories() {
        LOGGER.debug("fetching categories");
        try {
            List<Tag> list = tagService.getAllTags();
            LOGGER.debug("fetching categories succeeded with {} items", list.size());
            list.sort((a, b) -> a.getName().compareTo(b.getName()));
            list.add(null);
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("fetching categories failed", ex);
            ErrorDialog.show(root, "Fehler beim Laden", "Foto-Kategorien konnten nicht geladen werden.");
            return new ArrayList<>();
        }
    }

    private List<Photographer> getAllPhotographers() {
        LOGGER.debug("fetching photographers");
        try {
            List<Photographer> list = photographerService.readAll();
            LOGGER.debug("fetching photographers succeeded with {} items", list.size());
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("fetching photographers failed", ex);
            ErrorDialog.show(root, "Fehler beim Laden", "Fotografen konnten nicht geladen werden.");
            return new ArrayList<>();
        }
    }

    private List<Journey> getAllJourneys() {
        LOGGER.debug("fetching journeys");
        try {
            List<Journey> list = clusterService.getAllJourneys();
            list.sort((a, b) -> a.getName().compareTo(b.getName()));
            list.add(null);
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("fetching journeys failed", ex);
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Laden");
            dialog.setContentText("Reisen konnten nicht geladen werden.");
            dialog.showAndWait();
            return new ArrayList<>();
        }
    }

    private List<Place> getAllPlaces() {
        LOGGER.debug("fetching journeys");
        try {
            List<Place> list = clusterService.getAllPlaces();
            //list.sort((a, b) -> a.getName().compareTo(b.getName()));
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("fetching journeys failed", ex);
            ErrorDialog.show(root, "Fehler beim Laden", "Reisen konnten nicht geladen werden.");
            return new ArrayList<>();
        }
    }

    private void resetFilter() {
        // don't update the filter until all list views have been reset
        Runnable savedCallback = filterChangeCallback;
        filterChangeCallback = null;

        refreshLists();
        inspectorController.refresh();
        categoryListView.checkAll();
        ratingListView.checkAll();
        photographerListView.checkAll();
        journeyListView.checkAll();
        placeListView.checkAll();

        // restore the callback and handle the change
        filterChangeCallback = savedCallback;
    }
}
