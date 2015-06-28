package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.FilterControl;
import at.ac.tuwien.qse.sepm.gui.control.FilterGroup;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.Aggregator;
import at.ac.tuwien.qse.sepm.service.impl.PhotoSet;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoPathFilter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class OrganizerImpl implements Organizer {

    private static final Logger LOGGER = LogManager.getLogger(OrganizerImpl.class);
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy MMM");

    @Autowired private PhotographerService photographerService;
    @Autowired private ClusterService clusterService;
    @Autowired private Inspector<Photo> inspectorController;
    @Autowired private TagService tagService;
    @Autowired private WorkspaceService workspaceService;
    @Autowired private PhotoService photoService;

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
    @FXML private ChoiceBox<Path> directoryChoiceBox;
    @FXML private Button deleteFolderButton;

    private final PhotoSet allPhotos = new PhotoSet();
    private final PhotoSet acceptedPhotos = new PhotoSet();

    private PhotoFilter usedFilter = new PhotoFilter();
    private PhotoFilter photoFilter = usedFilter;
    private PhotoFilter folderFilter = new PhotoPathFilter();
    private Runnable filterChangeCallback;

    @Override public void setFilterChangeAction(Runnable callback) {
        LOGGER.debug("setting usedFilter change action");
        filterChangeCallback = callback;
    }

    @Override public void reset() {
        allPhotos.clear();
        acceptedPhotos.clear();
    }

    @Override public boolean accept(Photo photo) {
        remove(photo);
        allPhotos.add(photo);
        if (usedFilter.test(photo)) {
            acceptedPhotos.add(photo);
            return true;
        }
        return false;
    }

    @Override public void remove(Photo photo) {
        allPhotos.remove(photo);
        acceptedPhotos.remove(photo);
    }

    @FXML private void initialize() {
        folderTree.setOnMouseClicked(event -> handleFolderChange());

        listTabContent.visibleProperty().bind(listTab.selectedProperty());
        folderTabContent.visibleProperty().bind(folderTab.selectedProperty());
        listTab.setOnAction(event -> {
            listTab.setSelected(true);
            folderTab.setSelected(false);
            usedFilter = photoFilter;
            handleFilterChange();
        });
        folderTab.setOnAction(event -> {
            listTab.setSelected(false);
            folderTab.setSelected(true);
            usedFilter = new PhotoPathFilter();
            buildTreeView();
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
        photoService.subscribeCreate(this::handlePhotoAdded);

        deleteFolderButton.setOnAction(event -> handleDeleteDirectory());
    }

    @Override public void setWorldMapPlace(Place place) {
        placeFilter.excludeAll();
        placeFilter.include(place);
        handlePlacesChange();
    }

    private void handleDeleteDirectory() {
        Path directory = directoryChoiceBox.getSelectionModel().getSelectedItem();
        if (directory != null) {
            try {
                workspaceService.removeDirectory(directory);
            } catch (ServiceException ex) {
                LOGGER.error("Could not delete directory");
            }
        }
        buildTreeView();
    }

    private void buildTreeView() {
        Collection<Path> workspaceDirectories;
        try {
            workspaceDirectories = workspaceService.getDirectories();
        } catch (ServiceException ex) {
            workspaceDirectories = new LinkedList<>();
        }

        FilePathTreeItem root = new FilePathTreeItem(Paths.get("workspace"));
        root.setExpanded(true);
        folderTree.setRoot(root);
        folderTree.setShowRoot(false);
        for (Path dirPath : workspaceDirectories) {
            findFiles(dirPath.toFile(), root);
        }
        directoryChoiceBox.setItems(FXCollections.observableArrayList(workspaceDirectories));
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
        usedFilter.getRatingFilter().getIncluded().clear();
        usedFilter.getRatingFilter().getIncluded().addAll(ratingFilter.getIncludedValues());
        handleFilterChange();
    }
    private void handleCategoriesChange() {
        LOGGER.debug("category usedFilter changed");
        usedFilter.getTagFilter().getRequired().clear();
        usedFilter.getTagFilter().getRequired().addAll(tagFilter.getIncludedValues());
        handleFilterChange();
    }
    private void handlePhotographersChange() {
        LOGGER.debug("photographer usedFilter changed");
        usedFilter.getPhotographerFilter().getIncluded().clear();
        usedFilter.getPhotographerFilter().getIncluded().addAll(photographerFilter.getIncludedValues());
        handleFilterChange();
    }
    private void handleJourneysChange() {
        LOGGER.debug("journey usedFilter changed");
        usedFilter.getJourneyFilter().getIncluded().clear();
        usedFilter.getJourneyFilter().getIncluded().addAll(journeyFilter.getIncludedValues());
        handleFilterChange();
    }
    private void handlePlacesChange() {
        LOGGER.debug("place usedFilter changed");
        usedFilter.getPlaceFilter().getIncluded().clear();
        usedFilter.getPlaceFilter().getIncluded().addAll(placeFilter.getIncludedValues());
        handleFilterChange();
    }

    private List<Rating> getAllRatings() {
        LOGGER.debug("fetching ratings");
        List<Rating> list = new LinkedList<Rating>();
        list.add(Rating.NONE);
        list.add(Rating.GOOD);
        list.add(Rating.NEUTRAL);
        list.add(Rating.BAD);
        LOGGER.debug("fetching ratings succeeded with {} items", list.size());
        return list;
    }
    private List<Tag> getAllTags() {
        LOGGER.debug("fetching categories");
        try {
            List<Tag> list = tagService.getAllTags();
            LOGGER.debug("fetching categories succeeded with {} items", list.size());
            list.sort((a, b) -> a.getName().compareTo(b.getName()));
            list.add(0, null);
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
            list.add(0, null);
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
            list.add(0, null);
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
            list.sort((a, b) -> {
                int c = a.getCountry().compareTo(b.getCountry());
                if (c != 0)
                    return c;
                return a.getCity().compareTo(b.getCity());
            });
            list.add(0, null);
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
        refreshFilter(acceptedPhotos.getRatings(), ratingFilter, getAllRatings(), value -> {
            switch (value) {
                case GOOD:
                    return "Gut";
                case NEUTRAL:
                    return "Neutral";
                case BAD:
                    return "Schlecht";
                default:
                    return "Keine Bewertung";
            }
        });
    }
    private void refreshTags() {
        refreshFilter(acceptedPhotos.getTags(), tagFilter, getAllTags(), value -> {
            if (value == null)
                return "Keine Kategorien";
            return value.getName();
        });
    }
    private void refreshJourneys() {
        refreshFilter(acceptedPhotos.getJourneys(), journeyFilter, getAllJourneys(), value -> {
            if (value == null)
                return "Keine Reise";
            return value.getName();
        });
    }
    private void refreshPlaces() {
        refreshFilter(acceptedPhotos.getPlaces(), placeFilter, getAllPlaces(), value -> {
            if (value == null)
                return "Kein Ort";
            return value.getCountry() + ", " + value.getCity();
        });
    }
    private void refreshPhotographers() {
        refreshFilter(acceptedPhotos.getPhotographers(), photographerFilter, getAllPhotographers(),
                value -> {
                    if (value == null)
                        return "Kein Fotograf";
                    return value.getName();
                });
    }

    private static <T> void refreshFilter(Aggregator<T> aggregator, FilterGroup<T> filterGroup, Iterable<T> values, Function<T, String> converter) {
        Platform.runLater(() -> {
            // NOTE: Remember the values that were excluded before the refresh and exclude them.
            // That way the filter stays the same and new values are included automatically.
            Set<T> excluded = filterGroup.getExcludedValues();
            filterGroup.getItems().clear();
            values.forEach(p -> {
                FilterControl<T> filter = new FilterControl<>();
                filter.setValue(p);
                filter.setConverter(converter);
                filter.setIncluded(!excluded.contains(p));
                filter.setCount(aggregator.getCount(p));
                filterGroup.getItems().add(filter);
            });
            LOGGER.info("refreshing filter");
        });
    }

    private void handlePhotoAdded(Photo photo) {
        Platform.runLater(() -> {
            FilePathTreeItem root = (FilePathTreeItem) folderTree.getRoot();

            Path directory = photo.getFile().getParent();

            if (isPathAlreadyKnown(directory, root)) {
                return;
            }

            buildTreeView();
        });
    }
    private boolean isPathAlreadyKnown(Path directory, FilePathTreeItem node) {
        if (node == null) {
            return false;
        }

        if (node.getFullPath().equals(directory.toString())) {
            return true;
        }

        for (TreeItem<String> child : node.getChildren()) {
            FilePathTreeItem item = (FilePathTreeItem)child;

            if (isPathAlreadyKnown(directory, item)) {
                return true;
            }
        }

        return false;
    }
}
