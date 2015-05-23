package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
    @FXML private TitledPane ratingPane;
    @FXML private TitledPane categoryPane;
    @FXML private TitledPane photographerPane;
    @FXML private TitledPane monthPane;

    private final CheckListView<Rating> ratingListView = new CheckListView<>();
    private final CheckListView<Tag> categoryListView = new CheckListView<>();
    private final CheckListView<Photographer> photographerListView = new CheckListView<>();
    private final CheckListView<YearMonth> monthListView = new CheckListView<>();

    private final ObservableList<Rating> ratingList = FXCollections.observableArrayList();
    private final ObservableList<Tag> categoryList = FXCollections.observableArrayList();
    private final ObservableList<Photographer> photographerList = FXCollections.observableArrayList();
    private final ObservableList<YearMonth> monthList = FXCollections.observableArrayList();
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

        ratingPane.setContent(ratingListView);
        categoryPane.setContent(categoryListView);
        photographerPane.setContent(photographerListView);
        monthPane.setContent(monthListView);

        ratingListView.setItems(ratingList);
        ratingListView.getCheckModel().getCheckedItems().addListener(this::handleRatingsChange);
        ratingListView.setCellFactory(list -> new CheckBoxListCell<>(
            item -> ratingListView.getItemBooleanProperty(item),
            new StringConverter<Rating>() {
                @Override public Rating fromString(String string) {
                    return null;
                }
                @Override public String toString(Rating item) {
                    switch (item) {
                        case GOOD: return "Gut";
                        case NEUTRAL: return "Neutral";
                        case BAD: return "Schlecht";
                        default: return "Unbewertet";
                    }
                }
            })
        );

        SortedList<Tag> sortedCategoryList = new SortedList<>(categoryList);
        sortedCategoryList.setComparator((a, b) -> a.getName().compareTo(b.getName()));
        categoryListView.setItems(sortedCategoryList);
        categoryListView.getCheckModel().getCheckedItems().addListener(this::handleCategoriesChange);
        categoryListView.setCellFactory(list -> new CheckBoxListCell<>(
            item -> categoryListView.getItemBooleanProperty(item),
            new StringConverter<Tag>() {
                @Override public Tag fromString(String string) {
                    return null;
                }
                @Override public String toString(Tag item) {
                    return item.getName();
                }
            })
        );

        SortedList<Photographer> sortedPhotographerList = new SortedList<>(photographerList);
        sortedPhotographerList.setComparator((a, b) -> a.getName().compareTo(b.getName()));
        photographerListView.setItems(sortedPhotographerList);
        photographerListView.getCheckModel().getCheckedItems().addListener(this::handlePhotographersChange);
        photographerListView.setCellFactory(list -> new CheckBoxListCell<>(
            item -> photographerListView.getItemBooleanProperty(item),
            new StringConverter<Photographer>() {
                @Override public Photographer fromString(String string) {
                    return null;
                }
                @Override public String toString(Photographer item) {
                    return item.getName();
                }
            })
        );

        SortedList<YearMonth> sortedMonthList = new SortedList<>(monthList);
        sortedMonthList.setComparator((a, b) -> b.compareTo(a));
        monthListView.setItems(sortedMonthList);
        monthListView.getCheckModel().getCheckedItems().addListener(this::handleMonthsChange);
        monthListView.setCellFactory(list -> new CheckBoxListCell<>(
            item -> monthListView.getItemBooleanProperty(item),
            new StringConverter<YearMonth>() {
                @Override public YearMonth fromString(String string) {
                    return null;
                }
                @Override public String toString(YearMonth item) {
                    return monthFormatter.format(item);
                }
            })
        );

        refreshLists();
    }

    private void handleFilterChange() {
        LOGGER.debug("filter changed");
        if (filterChangeCallback == null) return;
        filterChangeCallback.accept(getFilter());
    }
    private void handleRatingsChange(ListChangeListener.Change<? extends Rating> change) {
        LOGGER.debug("rating filter changed");
        filter.getIncludedRatings().clear();
        filter.getIncludedRatings().addAll(ratingListView.getCheckModel().getCheckedItems());
        handleFilterChange();
    }
    private void handleCategoriesChange(ListChangeListener.Change<? extends Tag> change) {
        LOGGER.debug("category filter changed");
        filter.getIncludedCategories().clear();
        filter.getIncludedCategories().addAll(categoryListView.getCheckModel().getCheckedItems());
        handleFilterChange();
    }
    private void handlePhotographersChange(ListChangeListener.Change<? extends Photographer> change) {
        LOGGER.debug("photographer filter changed");
        filter.getIncludedPhotographers().clear();
        filter.getIncludedPhotographers().addAll(
                photographerListView.getCheckModel().getCheckedItems());
        handleFilterChange();
    }
    private void handleMonthsChange(ListChangeListener.Change<? extends YearMonth> change) {
        LOGGER.debug("month filter changed");
        filter.getIncludedMonths().clear();
        filter.getIncludedMonths().addAll(monthListView.getCheckModel().getCheckedItems());
        handleFilterChange();
    }

    private void refreshLists() {
        LOGGER.debug("refreshing filter");
        ratingList.clear();
        categoryList.clear();
        photographerList.clear();
        monthList.clear();

        ratingList.addAll(getAllRatings());
        categoryList.addAll(getAllCategories());
        photographerList.addAll(getAllPhotographers());
        monthList.addAll(getAllMonths());
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
}
