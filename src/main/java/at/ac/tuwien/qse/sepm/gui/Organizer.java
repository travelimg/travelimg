package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.function.Consumer;

/**
 * Controller for organizer view which is used for browsing photos by month.
 *
 * TODO: Decide whether to call it Organizer or Browser or something else.
 */
public class Organizer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private PhotoService photoService;
    @Autowired private PhotographerService photographerService;
    @Autowired private TagService tagService;

    @FXML private BorderPane root;
    @FXML private Button importButton;
    @FXML private Button presentButton;

    @FXML private VBox filterContainer;
    @FXML private FilterList<Rating> ratingListView;
    @FXML private FilterList<Tag> categoryListView;
    @FXML private FilterList<Photographer> photographerListView;
    @FXML private FilterList<YearMonth> monthListView;

    @FXML private Button resetButton;

    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy MMM");
    private final PhotoFilter filter = new PhotoFilter();
    private Consumer<PhotoFilter> filterChangeCallback;

    public void setPresentAction(Runnable callback) {
        LOGGER.debug("setting present action");
        presentButton.setOnAction(event -> callback.run());
    }

    public void setImportAction(Runnable callback) {
        LOGGER.debug("setting import action");
        importButton.setOnAction(event -> callback.run());
    }

    public void setFilterChangeAction(Consumer<PhotoFilter> callback) {
        LOGGER.debug("setting filter change action");
        filterChangeCallback = callback;
    }

    public PhotoFilter getFilter() {
        return new PhotoFilter(filter);
    }

    @FXML
    private void initialize() {

        resetButton.setOnAction(event -> resetFilter());

        ratingListView = new FilterList<>(value -> {
            switch (value) {
                case GOOD: return "Gut";
                case NEUTRAL: return "Neutral";
                case BAD: return "Schlecht";
                default: return "Unbewertet";
            }
        });
        ratingListView.setTitle("Bewertungen");
        ratingListView.setChangeHandler(this::handleRatingsChange);
        categoryListView = new FilterList<>(value -> value.getName());
        categoryListView.setTitle("Kategorien");
        categoryListView.setChangeHandler(this::handleCategoriesChange);
        photographerListView = new FilterList<>(value -> value.getName());
        photographerListView.setTitle("Fotografen");
        photographerListView.setChangeHandler(this::handlePhotographersChange);
        monthListView = new FilterList<>(value -> monthFormatter.format(value));
        monthListView.setTitle("Monate");
        monthListView.setChangeHandler(this::handleMonthsChange);
        filterContainer.getChildren().addAll(ratingListView, categoryListView, photographerListView,
                monthListView);

        refreshLists();
        resetFilter();
    }

    private void handleFilterChange() {
        LOGGER.debug("filter changed");
        if (filterChangeCallback == null) return;
        filterChangeCallback.accept(getFilter());
    }
    private void handleRatingsChange(List<Rating> values) {
        LOGGER.debug("rating filter changed");
        filter.getIncludedRatings().clear();
        filter.getIncludedRatings().addAll(values);
        handleFilterChange();
    }
    private void handleCategoriesChange(List<Tag> values) {
        LOGGER.debug("category filter changed");
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
    private void handleMonthsChange(List<YearMonth> values) {
        LOGGER.debug("month filter changed");
        filter.getIncludedMonths().clear();
        filter.getIncludedMonths().addAll(values);
        handleFilterChange();
    }

    private void refreshLists() {
        LOGGER.debug("refreshing filter");

        ratingListView.setValues(getAllRatings());
        categoryListView.setValues(getAllCategories());
        photographerListView.setValues(getAllPhotographers());
        monthListView.setValues(getAllMonths());
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
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("fetching categories failed", ex);
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Laden");
            dialog.setContentText("Foto-Kategorien konnten nicht geladen werden.");
            dialog.showAndWait();
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
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Laden");
            dialog.setContentText("Fotografen konnten nicht geladen werden.");
            dialog.showAndWait();
            return new ArrayList<>();
        }
    }
    private List<YearMonth> getAllMonths() {
        LOGGER.debug("fetching months");
        try {
            List<YearMonth> list = photoService.getMonthsWithPhotos();
            LOGGER.debug("fetching months\" succeeded with {} items", list.size());
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("fetching months\" failed", ex);
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Laden");
            dialog.setContentText("Monate konnten nicht geladen werden.");
            dialog.showAndWait();
            return new ArrayList<>();
        }
    }

    private void resetFilter() {
        refreshLists();
        categoryListView.checkAll();
        ratingListView.checkAll();
        photographerListView.checkAll();
        monthListView.checkAll();
    }
}
