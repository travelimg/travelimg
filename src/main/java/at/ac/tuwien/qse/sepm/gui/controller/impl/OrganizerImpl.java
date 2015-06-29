package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.FilterControl;
import at.ac.tuwien.qse.sepm.gui.control.FilterGroup;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.gui.util.BufferedBatchOperation;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.Aggregator;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoPathFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoSet;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class OrganizerImpl extends Refresher implements Organizer {

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

    private final PhotoSet allPhotos = new PhotoSet();
    private final PhotoSet acceptedPhotos = new PhotoSet();

    private PhotoFilter usedFilter = new PhotoFilter();
    private PhotoFilter photoFilter = usedFilter;
    private PhotoFilter folderFilter = new PhotoPathFilter();
    private Runnable filterChangeCallback;
    private boolean suppressChangeEvent = false;

    private BufferedBatchOperation<Photo> addOperation;

    @Autowired public void setScheduler(ScheduledExecutorService scheduler) {
        addOperation = new BufferedBatchOperation<>(photos -> {
            Platform.runLater(() -> {/*TODO*/});
        }, scheduler);
    }

    @Override public void setFilterChangeAction(Runnable callback) {
        LOGGER.debug("setting usedFilter change action");
        filterChangeCallback = callback;
    }

    @Override public void reset() {
        allPhotos.clear();
        acceptedPhotos.clear();
        markDirty();
    }

    @Override public boolean accept(Photo photo) {
        remove(photo);
        allPhotos.add(photo);
        if (usedFilter.test(photo)) {
            acceptedPhotos.add(photo);
            markDirty();
            return true;
        }
        markDirty();
        return false;
    }

    @Override public void remove(Photo photo) {
        allPhotos.remove(photo);
        acceptedPhotos.remove(photo);
        markDirty();
    }

    @Override public void setWorldMapPlace(Place place){
        placeFilter.excludeAll();
        placeFilter.include(place);
        handlePlacesChange();
    }

    @Override protected void refresh() {
        LOGGER.debug("refreshing filter");
        refreshRatings();
        refreshTags();
        refreshJourneys();
        refreshPlaces();
        refreshPhotographers();
    }

    @FXML private void initialize() {

        initializeFilesTree();

        folderTree.setOnMouseClicked(event -> handleFolderChange());

        listTabContent.visibleProperty().bind(listTab.selectedProperty());
        folderTabContent.visibleProperty().bind(folderTab.selectedProperty());
        listTab.setOnAction(event -> filterViewClicked());
        folderTab.setOnAction(event -> folderViewClicked());

        ratingFilter.setOnUpdate(this::handleRatingsChange);
        tagFilter.setOnUpdate(this::handleCategoriesChange);
        photographerFilter.setOnUpdate(this::handlePhotographersChange);
        journeyFilter.setOnUpdate(this::handleJourneysChange);
        placeFilter.setOnUpdate(this::handlePlacesChange);

        refresh();
        listTab.fire();

        tagService.subscribeTagChanged((p) -> refreshTags());
        clusterService.subscribeJourneyChanged((p) -> refreshJourneys());
        clusterService.subscribePlaceChanged((p) -> refreshPlaces());
        photographerService.subscribeChanged((p) -> refreshPhotographers());
        photoService.subscribeCreate(this::handlePhotoAdded);

        start(1, TimeUnit.SECONDS);
    }

    private void initializeFilesTree() {
        folderTree.setOnMouseClicked(event -> handleFolderChange());
        folderTree.setCellFactory(treeView -> {
            HBox hbox = new HBox();
            hbox.setMaxWidth(200);
            hbox.setPrefWidth(200);
            hbox.setSpacing(7);

            FontAwesomeIconView openFolderIcon = new FontAwesomeIconView(
                    FontAwesomeIcon.FOLDER_OPEN_ALT);
            openFolderIcon.setTranslateY(7);
            FontAwesomeIconView closedFolderIcon = new FontAwesomeIconView(
                    FontAwesomeIcon.FOLDER_ALT);
            closedFolderIcon.setTranslateY(7);

            Label dirName = new Label();
            dirName.setMaxWidth(150);

            FontAwesomeIconView removeIcon = new FontAwesomeIconView(FontAwesomeIcon.REMOVE);

            Tooltip deleteToolTip = new Tooltip();
            deleteToolTip.setText("Verzeichnis aus Workspace entfernen");

            Button button = new Button(null, removeIcon);
            button.setTooltip(deleteToolTip);
            button.setTranslateX(8);

            return new TreeCell<String>() {
                @Override public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                        setText(null);
                    } else if (getTreeItem() instanceof FilePathTreeItem) {
                        hbox.getChildren().clear();
                        dirName.setText(item);
                        if (getTreeItem().isExpanded()) {
                            hbox.getChildren().add(openFolderIcon);
                        } else {
                            hbox.getChildren().add(closedFolderIcon);
                        }
                        hbox.getChildren().add(dirName);
                        TreeItem<String> treeItem = getTreeItem();
                        TreeItem<String> parent = treeItem != null ? treeItem.getParent() : null;
                        if (parent != null && parent.equals(folderTree.getRoot())) {
                            String path = ((FilePathTreeItem) getTreeItem()).getFullPath();
                            button.setOnAction(event -> handleDeleteDirectory(Paths.get(path)));
                            hbox.getChildren().add(button);
                        }
                        setGraphic(hbox);
                    }
                }
            };
        });
    }

    private void handleDeleteDirectory(Path directory) {
        if (directory != null) {
            try {
                workspaceService.removeDirectory(directory);
            } catch (ServiceException ex) {
                LOGGER.error("Could not delete directory");
            }
        }
    }

    // This Method let's the User switch to the usedFilter-view
    private void filterViewClicked() {listTab.setSelected(true);
        folderTab.setSelected(false);
        usedFilter = photoFilter;
        handleFilterChange();
    }

    // This Method let's the User switch to the folder-view
    private void folderViewClicked() {
        listTab.setSelected(false);
        folderTab.setSelected(true);
        usedFilter = new PhotoPathFilter();
        buildTreeView();
        handleFilterChange();
    }

    private void buildTreeView() {
        Collection<Path> workspaceDirectories;
        try {
            workspaceDirectories = workspaceService.getDirectories();
            LOGGER.debug("found directories {}", workspaceDirectories);
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
        if (filterChangeCallback == null || suppressChangeEvent)
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

    private void refreshRatings() {
        List<Rating> list = new ArrayList<>(4);
        list.add(Rating.NONE);
        list.add(Rating.GOOD);
        list.add(Rating.NEUTRAL);
        list.add(Rating.BAD);
        photoFilter.getRatingFilter().getIncluded().addAll(refreshFilterList(
                list,
                acceptedPhotos.getRatings(),
                ratingFilter,
                "Keine Kategorien",
                value -> {
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
                }));
    }
    private void refreshTags() {
        photoFilter.getTagFilter().getRequired().clear();
        refreshFilterListExcluded(
                allPhotos.getTags(),
                acceptedPhotos.getTags(),
                tagFilter,
                "Keine Kategorien",
                Tag::getName);
    }
    private void refreshJourneys() {
        photoFilter.getJourneyFilter().getIncluded().addAll(refreshFilterList(
                allPhotos.getJourneys(),
                acceptedPhotos.getJourneys(),
                journeyFilter,
                "Keine Reise",
                Journey::getName));
    }
    private void refreshPlaces() {
        photoFilter.getPlaceFilter().getIncluded().addAll(refreshFilterList(
                allPhotos.getPlaces(),
                acceptedPhotos.getPlaces(),
                placeFilter,
                "Kein Ort",
                (p) -> p.getCountry() + ", " + p.getCity()));
    }
    private void refreshPhotographers() {
        photoFilter.getPhotographerFilter().getIncluded().addAll(refreshFilterList(
                allPhotos.getPhotographers(),
                acceptedPhotos.getPhotographers(),
                photographerFilter,
                "Kein Fotograf",
                Photographer::getName));
    }

    private <T> Collection<T> refreshFilterList(
            Iterable<T> values,
            Aggregator<T> aggregator,
            FilterGroup<T> filter,
            String defaultLabel,
            Function<T, String> converter) {

        // Sort values alphabetically.
        List<T> list = new LinkedList<>();
        values.forEach(list::add);
        list.remove(null);
        list.sort((a, b) -> converter.apply(a).compareTo(converter.apply(b)));

        // Default value is at the beginning.
        list.add(0, null);

        Platform.runLater(() -> {
            // NOTE: Remember the values that were excluded before the refresh and exclude them.
            // That way the filter stays the same and new values are included automatically.
            Set<T> excluded = filter.getExcludedValues();
            filter.getItems().clear();
            values.forEach(p -> {
                FilterControl<T> item = new FilterControl<>();
                item.setValue(p);
                item.setConverter(val -> {
                    if (val == null) {
                        return defaultLabel;
                    }
                    return converter.apply(val);
                });
                item.setIncluded(!excluded.contains(p));
                item.setCount(aggregator.getCount(p));
                filter.getItems().add(item);
            });
        });

        return list;
    }

    private <T> Collection<T> refreshFilterListExcluded(
            Iterable<T> values,
            Aggregator<T> aggregator,
            FilterGroup<T> filter,
            String defaultLabel,
            Function<T, String> converter) {

        // Sort values alphabetically.
        List<T> list = new LinkedList<>();
        values.forEach(list::add);
        list.remove(null);
        list.sort((a, b) -> converter.apply(a).compareTo(converter.apply(b)));

        // Default value is at the beginning.
        list.add(0, null);

        Platform.runLater(() -> {
            // NOTE: Remember the values that were excluded before the refresh and exclude them.
            // That way the filter stays the same and new values are included automatically.
            Set<T> included = filter.getIncludedValues();
            filter.getItems().clear();
            values.forEach(p -> {
                FilterControl<T> item = new FilterControl<>();
                item.setValue(p);
                item.setConverter(val -> {
                    if (val == null) {
                        return defaultLabel;
                    }
                    return converter.apply(val);
                });
                item.setIncluded(included.contains(p));
                item.setCount(aggregator.getCount(p));
                filter.getItems().add(item);
            });
        });

        return list;
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
        addOperation.add(photo);
    }
    private boolean isPathAlreadyKnown(Path directory, FilePathTreeItem node) {
        if (node == null) {
            return false;
        }

        if (node.getFullPath().equals(directory.toString())) {
            return true;
        }

        for (TreeItem<String> child : node.getChildren()) {
            FilePathTreeItem item = (FilePathTreeItem) child;

            if (isPathAlreadyKnown(directory, item)) {
                return true;
            }
        }

        return false;
    }
}
