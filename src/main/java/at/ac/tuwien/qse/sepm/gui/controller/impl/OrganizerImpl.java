package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.Filter;
import at.ac.tuwien.qse.sepm.gui.control.FilterGroup;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoPathFilter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * Controller for organizer view which is used for browsing photos by month.
 */
public class OrganizerImpl implements Organizer {

    private static final Logger LOGGER = LogManager.getLogger(OrganizerImpl.class);
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy MMM");

    @Autowired private PhotographerService photographerService;
    @Autowired private ClusterService clusterService;
    @Autowired private Inspector<Photo> inspectorController;
    @Autowired private TagService tagService;

    @FXML private BorderPane root;
    @FXML private FilterGroup<Rating> ratingFilter;
    @FXML private FilterGroup<Tag> tagFilter;
    @FXML private FilterGroup<Photographer> photographerFilter;
    @FXML private FilterGroup<Journey> journeyFilter;
    @FXML private FilterGroup<Place> placeFilter;

    @FXML private ToggleButton listTab;
    @FXML private ToggleButton folderTab;
    @FXML private Node listTabContent;
    @FXML private Node folderTabContent;

    @FXML private TreeView<String> folderTree;

    private PhotoFilter usedFilter = new PhotoFilter();
    private PhotoFilter photoFilter = usedFilter;
    private PhotoFilter folderFilter = new PhotoPathFilter();
    private Runnable filterChangeCallback;

    @Override public void setFilterChangeAction(Runnable callback) {
        LOGGER.debug("setting usedFilter change action");
        filterChangeCallback = callback;
    }

    @Override public PhotoFilter getUsedFilter() {
        return usedFilter;
    }

    @FXML private void initialize() {
        folderTree.setOnMouseClicked(event -> handleFolderChange());

        listTabContent.visibleProperty().bind(listTab.selectedProperty());
        folderTabContent.visibleProperty().bind(folderTab.selectedProperty());
        listTab.setOnAction(event -> {
            listTab.setSelected(true);
            folderTab.setSelected(false);
            handleFilterChange();
        });
        folderTab.setOnAction(event -> {
            listTab.setSelected(false);
            folderTab.setSelected(true);

            Path rootDirectories = Paths.get(System.getProperty("user.home"), "/travelimg");
            findFiles(rootDirectories.toFile(), null);
            usedFilter = new PhotoPathFilter();
            handleFilterChange();
        });

        ratingFilter.setOnUpdate(this::handleRatingsChange);
        tagFilter.setOnUpdate(this::handleCategoriesChange);
        photographerFilter.setOnUpdate(this::handlePhotographersChange);
        journeyFilter.setOnUpdate(this::handleJourneysChange);
        placeFilter.setOnUpdate(this::handlePlacesChange);

        refresh();
        resetFilter();
        listTab.fire();

        tagService.subscribeTagChanged((p) -> refreshTags());
        clusterService.subscribeJourneyChanged((p) -> refreshJourneys());
        clusterService.subscribePlaceChanged((p) -> refreshPlaces());
        photographerService.subscribeChanged((p) -> refreshPhotographers());
    }

    @Override public void setWorldMapPlace(Place place) {
        placeFilter.excludeAll();
        placeFilter.include(place);
        handlePlacesChange();
    }

    private void findFiles(File dir, FilePathTreeItem parent) {
        FilePathTreeItem rootNode = new FilePathTreeItem(dir.toPath());
        rootNode.setExpanded(true);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                findFiles(file, rootNode);
            }
        }
        if (parent == null) {
            folderTree.setRoot(rootNode);
        } else {
            parent.getChildren().add(rootNode);
        }
    }

    private void handleFilterChange() {
        LOGGER.debug("usedFilter changed");
        if (filterChangeCallback == null)
            return;
        filterChangeCallback.run();
    }
    private void handleFolderChange() {
        LOGGER.debug("handle folder");
        FilePathTreeItem item = (FilePathTreeItem) folderTree.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (usedFilter instanceof PhotoPathFilter)
                ((PhotoPathFilter) usedFilter).setIncludedPath(Paths.get(item.getFullPath()));
            handleFilterChange();
        }
    }

    private void handleRatingsChange() {
        LOGGER.debug("rating usedFilter changed");
        usedFilter.getIncludedRatings().clear();
        usedFilter.getIncludedRatings().addAll(ratingFilter.getIncludedValues());
        handleFilterChange();
    }
    private void handleCategoriesChange() {
        LOGGER.debug("category usedFilter changed");
        Set<Tag> included = tagFilter.getIncludedValues();
        usedFilter.setUntaggedIncluded(included.contains(null));
        included.remove(null);
        usedFilter.getIncludedCategories().clear();
        usedFilter.getIncludedCategories().addAll(included);
        handleFilterChange();
    }
    private void handlePhotographersChange() {
        LOGGER.debug("photographer usedFilter changed");
        usedFilter.getIncludedPhotographers().clear();
        usedFilter.getIncludedPhotographers().addAll(photographerFilter.getIncludedValues());
        handleFilterChange();
    }
    private void handleJourneysChange() {
        LOGGER.debug("journey usedFilter changed");
        usedFilter.getIncludedJourneys().clear();
        usedFilter.getIncludedJourneys().addAll(journeyFilter.getIncludedValues());
        handleFilterChange();
    }
    private void handlePlacesChange() {
        LOGGER.debug("place usedFilter changed");
        usedFilter.getIncludedPlaces().clear();
        usedFilter.getIncludedPlaces().addAll(placeFilter.getIncludedValues());
        handleFilterChange();
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
    private List<Tag> getAllTags() {
        LOGGER.debug("fetching categories");
        try {
            List<Tag> list = tagService.getAllTags();
            LOGGER.debug("fetching categories succeeded with {} items", list.size());
            list.sort((a, b) -> a.getName().compareTo(b.getName()));
            list.add(null);
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("fetching categories failed", ex);
            ErrorDialog.show(root, "Fehler beim Laden",
                    "Foto-Kategorien konnten nicht geladen werden.");
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
        // don't update the usedFilter until all list views have been reset
        Runnable savedCallback = filterChangeCallback;
        filterChangeCallback = null;

        refresh();
        inspectorController.refresh();
        tagFilter.includeAll();
        ratingFilter.includeAll();
        photographerFilter.includeAll();
        journeyFilter.includeAll();
        placeFilter.includeAll();

        // restore the callback and handle the change
        filterChangeCallback = savedCallback;
    }

    private void refresh() {
        LOGGER.debug("refreshing filter");

        refreshRatings();
        refreshTags();
        refreshJourneys();
        refreshPlaces();
        refreshPhotographers();
    }
    private void refreshRatings() {
        refreshFilter(ratingFilter, getAllRatings(), value -> {
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
    }
    private void refreshTags() {
        refreshFilter(tagFilter, getAllTags(), value -> {
            if (value == null) return "Keinen Kategorien zugehörig";
            return value.getName();
        });
    }
    private void refreshJourneys() {
        refreshFilter(journeyFilter, getAllJourneys(), value -> {
            if (value == null) return "Keiner Reise zugehörig";
            return value.getName();
        });
    }
    private void refreshPlaces() {
        refreshFilter(placeFilter, getAllPlaces(), value -> {
            if (value == null) return "Keinem Ort zugehörig";
            return value.getCountry() + ", " + value.getCity();
        });
    }
    private void refreshPhotographers() {
        refreshFilter(photographerFilter, getAllPhotographers(), value -> {
            if (value == null) return "Keinem Fotografen zugehörig";
            return value.getName();
        });
    }

    private static <T> void refreshFilter(FilterGroup<T> filterGroup, Collection<T> values, Function<T, String> converter) {
        Platform.runLater(() -> {
            // NOTE: Remember the values that were excluded before the refresh and exclude them.
            // That way the filter stays the same and new values are included automatically.
            Set<T> excluded = filterGroup.getExcludedValues();
            filterGroup.getItems().clear();
            values.forEach(p -> {
                Filter<T> filter = new Filter<>();
                filter.setValue(p);
                filter.setConverter(converter);
                filter.setIncluded(!excluded.contains(p));
                filterGroup.getItems().add(filter);
            });
            LOGGER.info("refreshing filter");
        });
    }
}
