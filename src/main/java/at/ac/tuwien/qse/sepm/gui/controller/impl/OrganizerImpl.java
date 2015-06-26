package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.FilterList;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import at.ac.tuwien.qse.sepm.service.impl.PhotoPathFilter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
    @Autowired private WorkspaceService workspaceService;

    @FXML private BorderPane root;
    @FXML private VBox filterContainer;
    @FXML private FilterList<Rating> ratingListView;
    @FXML private FilterList<Tag> categoryListView;
    @FXML private FilterList<Photographer> photographerListView;
    @FXML private FilterList<Journey> journeyListView;
    @FXML private FilterList<Place> placeListView;

    private ToggleButton folderViewButton = new ToggleButton("Ordneransicht");
    private ToggleButton filterViewButton = new ToggleButton("Filteransicht");

    @FXML private TreeView<String> filesTree;

    private HBox buttonBox;

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
        filesTree = new TreeView<>();
        filesTree.setOnMouseClicked(event -> handleFolderChange());
        folderViewButton.setOnAction(event -> folderViewClicked());
        filterViewButton.setOnAction(event -> filterViewClicked());

        ToggleGroup toggleGroup = new ToggleGroup();
        filterViewButton.setToggleGroup(toggleGroup);
        folderViewButton.setToggleGroup(toggleGroup);


        buttonBox = new HBox(filterViewButton, folderViewButton);
        buttonBox.setAlignment(Pos.CENTER);

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
            if (value == null)
                return "Nicht kategorisiert";
            return value.getName();
        });
        categoryListView.setTitle("Kategorien");
        categoryListView.setChangeHandler(this::handleCategoriesChange);
        photographerListView = new FilterList<>(value -> value.getName());
        photographerListView.setTitle("Fotografen");
        photographerListView.setChangeHandler(this::handlePhotographersChange);
        journeyListView = new FilterList<>(value -> {
            if (value == null)
                return "Keiner Reise zugeordnet";
            return value.getName();
        });
        journeyListView.setTitle("Reisen");
        journeyListView.setChangeHandler(this::handleJourneysChange);
        placeListView = new FilterList<Place>(value -> {
            if (value == null)
                return "Keinem Ort zugeordnet";
            return value.getCountry() + ", " + value.getCity();
        });
        placeListView.setTitle("Orte");
        placeListView.setChangeHandler(this::handlePlacesChange);

        filterContainer.getChildren()
                .addAll(buttonBox, ratingListView, categoryListView, photographerListView,
                        journeyListView, placeListView);

        refreshLists();
        resetFilter();
    }

    // This Method let's the User switch to the usedFilter-view
    private void filterViewClicked() {
        filterContainer.getChildren().clear();
        filterContainer.getChildren()
                .addAll(buttonBox, ratingListView, categoryListView, photographerListView,
                        journeyListView, placeListView);
        usedFilter = photoFilter;
        handleFilterChange();
    }

    // This Method let's the User switch to the folder-view
    private void folderViewClicked() {
        LOGGER.debug("Switch view");
        filterContainer.getChildren().clear();
        Collection<Path> workspaceDirectories;
        try {
            workspaceDirectories = workspaceService.getDirectories();
        } catch (ServiceException ex) {
            workspaceDirectories = new LinkedList<>();
        }

        FilePathTreeItem root = new FilePathTreeItem(Paths.get("workspace"));
        root.setExpanded(true);
        filesTree.setRoot(root);
        filesTree.setShowRoot(false);
        for (Path dirPath : workspaceDirectories) {
            findFiles(dirPath.toFile(), root);
        }

        filterContainer.getChildren().addAll(buttonBox, filesTree);
        VBox.setVgrow(filesTree, Priority.ALWAYS);
        usedFilter = new PhotoPathFilter();
        handleFilterChange();
    }

    private void findFiles(File dir, FilePathTreeItem parent) {
        FilePathTreeItem rootNode = new FilePathTreeItem(dir.toPath());
        rootNode.setExpanded(true);
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    findFiles(file, rootNode);
                }
            }
            if (parent == null) {
                filesTree.setRoot(rootNode);
            } else {
                parent.getChildren().add(rootNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        FilePathTreeItem item = (FilePathTreeItem) filesTree.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (usedFilter instanceof PhotoPathFilter)
                ((PhotoPathFilter) usedFilter).setIncludedPath(Paths.get(item.getFullPath()));
            handleFilterChange();
        }
        //        Path rootDirectories = Paths.get(System.getProperty("user.home"), "/travelimg");
        //        findFiles(rootDirectories.toFile(), null);
        //        filesTree.getSelectionModel().select(item);
    }

    private void handleRatingsChange(List<Rating> values) {
        LOGGER.debug("rating usedFilter changed");
        usedFilter.getIncludedRatings().clear();
        usedFilter.getIncludedRatings().addAll(values);
        handleFilterChange();
    }

    private void handleCategoriesChange(List<Tag> values) {
        LOGGER.debug("category usedFilter changed");
        usedFilter.setUntaggedIncluded(values.contains(null));
        values.remove(null);
        usedFilter.getIncludedCategories().clear();
        usedFilter.getIncludedCategories().addAll(values);
        handleFilterChange();
    }

    private void handlePhotographersChange(List<Photographer> values) {
        LOGGER.debug("photographer usedFilter changed");
        usedFilter.getIncludedPhotographers().clear();
        usedFilter.getIncludedPhotographers().addAll(values);
        handleFilterChange();
    }

    private void handleJourneysChange(List<Journey> values) {
        LOGGER.debug("journey usedFilter changed");
        usedFilter.getIncludedJourneys().clear();
        usedFilter.getIncludedJourneys().addAll(values);
        handleFilterChange();
    }

    private void handlePlacesChange(List<Place> values) {
        LOGGER.debug("place usedFilter changed");
        usedFilter.getIncludedPlaces().clear();
        usedFilter.getIncludedPlaces().addAll(values);
        handleFilterChange();
    }

    private void refreshLists() {
        LOGGER.debug("refreshing usedFilter");

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
