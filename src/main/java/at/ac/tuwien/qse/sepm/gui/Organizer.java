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

/**
 * Controller for organizer view which is used for browsing photos by month.
 *
 * TODO: Decide whether to call it Organizer or Browser or something else.
 */
public class Organizer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private ImportService importService;
    @Autowired private PhotoService photoService;
    @Autowired private PhotographerService photographerService;
    @Autowired private TagService tagService;
    @Autowired private MainController mainController;

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

    private Cancelable loadingTask;

    public Organizer() {

    }

    @FXML
    private void initialize() {

        ratingPane.setContent(ratingListView);
        categoryPane.setContent(categoryListView);
        photographerPane.setContent(photographerListView);
        monthPane.setContent(monthListView);

        importButton.setOnAction(this::handleImport);
        presentButton.setOnAction(this::handlePresent);

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
        reloadPhotos();
    }

    public void reloadPhotos() {

        // Cancel an older ongoing loading task.
        if (loadingTask != null) {
            loadingTask.cancel();
        }

        // Remove currently active photos.
        mainController.clearPhotos();

        // Load photos with current filter.
        this.loadingTask = photoService.loadPhotos(filter, this::handleLoadedPhoto,
                this::handleLoadError);
    }

    private void handleFilterChange() {
        reloadPhotos();
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
        LOGGER.debug("refreshFilter");
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
        LOGGER.debug("getAllRatings");
        List<Rating> list = new LinkedList<Rating>();
        list.add(Rating.GOOD);
        list.add(Rating.NEUTRAL);
        list.add(Rating.BAD);
        list.add(Rating.NONE);
        LOGGER.debug("getAllCategories succeeded with {} items", list.size());
        return list;
    }
    private List<Tag> getAllCategories() {
        LOGGER.debug("getAllCategories");
        try {
            List<Tag> list = tagService.getAllTags();
            LOGGER.debug("getAllCategories succeeded with {} items", list.size());
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("getAllCategories failed", ex);
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Laden");
            dialog.setContentText("Foto-Kategorien konnten nicht geladen werden.");
            dialog.showAndWait();
            return new ArrayList<>();
        }
    }
    private List<Photographer> getAllPhotographers() {
        LOGGER.debug("getAllPhotographers");
        try {
            List<Photographer> list = photographerService.readAll();
            LOGGER.debug("getAllPhotographers succeeded with {} items", list.size());
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("getAllPhotographers failed", ex);
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Laden");
            dialog.setContentText("Fotografen konnten nicht geladen werden.");
            dialog.showAndWait();
            return new ArrayList<>();
        }
    }
    private List<YearMonth> getAllMonths() {
        LOGGER.debug("getAllMonths");
        try {
            List<YearMonth> list = photoService.getMonthsWithPhotos();
            LOGGER.debug("getAllMonths succeeded with {} items", list.size());
            return list;
        } catch (ServiceException ex) {
            LOGGER.error("getAllMonths failed", ex);
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Laden");
            dialog.setContentText("Monate konnten nicht geladen werden.");
            dialog.showAndWait();
            return new ArrayList<>();
        }
    }

    private void handleImport(Event event) {
        ImportDialog dialog = new ImportDialog(root, photographerService);

        Optional<List<Photo>> photos = dialog.showForResult();
        if (!photos.isPresent()) return;

        importService.importPhotos(photos.get(), this::handleImportedPhoto, this::handleImportError);
    }

    private void handleImportError(Throwable error) {
        LOGGER.error("Import error", error);

        // queue an update in the main gui
        Platform.runLater(() -> {
            InfoDialog dialog = new InfoDialog(root, "Import Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Import fehlgeschlagen");
            dialog.setContentText("Fehlermeldung: " + error.getMessage());
            dialog.showAndWait();
        });
    }

    private void handleLoadError(Throwable error) {
        LOGGER.error("Load error", error);

        // queue an update in the main gui
        Platform.runLater(() -> {
            InfoDialog dialog = new InfoDialog(root, "Lade Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Laden von Fotos fehlgeschlagen");
            dialog.setContentText("Fehlermeldung: " + error.getMessage());
            dialog.showAndWait();
        });
    }

    /**
     * Called whenever a new photo is imported
     * @param photo The newly imported    photo
     */
    private void handleImportedPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {



            // Ignore photos that are not part of the current filter.
            if (!filter.matches(photo)) {
                return;
            }

            mainController.addPhoto(photo);
        });
    }

    /**
     * Called whenever a new photo is loaded from the service layer
     * @param photo The newly loaded photo
     */
    private void handleLoadedPhoto(Photo photo) {
        // queue an update in the main gui
        Platform.runLater(() -> {
                // Ignore photos that are not part of the current filter.
                if(!filter.matches(photo))
                    return;

                mainController.addPhoto(photo);
            }
        );
    }

    /**
     * Show the current photo selection in fullscreen.
     * @param event The event triggering the request.
     */
    private void handlePresent(Event event) {
        FullscreenWindow fullscreen = new FullscreenWindow();
        fullscreen.present(mainController.getActivePhotos());
    }

    /**
     * Get a list of months for which we currently possess photos.
     * @return A list of months for which photos are available
     */
    private List<YearMonth> getAvailableMonths() {
        List<YearMonth> months = new ArrayList<>();
        try {
            months = photoService.getMonthsWithPhotos();
        } catch (ServiceException ex) {
            // TODO: show error dialog
        }

        return months;
    }
}
