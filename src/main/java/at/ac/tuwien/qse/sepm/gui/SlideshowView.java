package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.grid.PaginatedImageGrid;
import at.ac.tuwien.qse.sepm.gui.grid.SlideshowGrid;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class SlideshowView {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private SlideService slideService;
    @Autowired private SlideshowService slideShowService;
    @Autowired
    private ImageCache imageCache;

    @FXML private BorderPane root;
    @FXML private ScrollPane gridContainer;
    @FXML private Button Btn_Add;
    @FXML private ComboBox cb_getSlideshows;
    @FXML private TextField tf_slideName;

    @Autowired
    private SlideshowOrganizer slideshowOrganizer;
    @Autowired
    private SlideshowService slideshowService;

    private SlideshowGrid grid = null;

    private ObservableList<Slideshow> slideshows = FXCollections.observableArrayList();

    private Integer slideshowCount=0;

    @Autowired
    public void setImageCache(ImageCache imageCache) {
        if (grid == null) {
            this.grid = new SlideshowGrid(imageCache);
            this.grid.setSlideChangedCallback(this::handleSlideChanged);
        }
    }

    public ObservableList<Slideshow> getSlideshows() {
        return slideshows;
    }

    public void addPhotosToSlideshow(List<Photo> photos, Slideshow slideshow) {
        try {
            slideshowService.addPhotosToSlideshow(photos, slideshow);

            Slideshow selected = slideshowOrganizer.getSelected();
            if (selected != null && selected.getId().equals(slideshow.getId())) {
                grid.setSlideshow(slideshow);
            }
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Hinzufügen zur Slideshow", "Fehlermeldung: " + ex.getMessage());
        }
    }

    @FXML
    private void initialize() {
        gridContainer.setContent(grid);

        Btn_Add.setOnAction(this::handlesetShowSlides);

        getAllSlideshowsToComboBox();

        slideshowOrganizer.setSlideshows(slideshows);
        slideshowOrganizer.getSelectedSlideshowProperty().addListener((observable, oldValue, newValue) -> {
            grid.setSlideshow(newValue);
        });

        loadAllSlideshows();
    }

    private void getAllSlideshowsToComboBox() {
        /*try {
            List<Slideshow> slideshows;
            slideshows = slideShowService.getAllSlideshows();

            for (int i = 0; i < slideshows.size(); i++)
                cb_getSlideshows.getItems().addAll(slideshows.get(i).getName());

        } catch (ServiceException e) {
            e.printStackTrace();
        }*/
    }

    private void createSlideshow() {
        try {
            Slideshow slideshow = new Slideshow();

            List<Slideshow> slideshows;
            //slideshows= slideShowService.getAllSlideshows();

            if (tf_slideName.getText().isEmpty()) {
                LOGGER.debug("Bitte geben Sie einen Namen für die Slideshow ein!");//TODO: Show an InfoBox
            } else {
                slideshow.setId(1);
                slideshow.setName(tf_slideName.getText());
                slideshow.setDurationBetweenPhotos(5.0);

                slideShowService.create(slideshow);
                cb_getSlideshows.getItems().add(tf_slideName.getText());
                tf_slideName.clear();
                LOGGER.info("Slideshow wurde korrekt angelegt!");
            }



        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }

    private void handlesetShowSlides(Event event) {
        createSlideshow();
    }

    private void handleSlideChanged(Slide slide) {
        try {
            slideService.update(slide);
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern der Slides", "Fehlermeldung: " + ex.getMessage());
        }
    }

    private void loadAllSlideshows() {
        try {
            slideshows.clear();
            slideshows.addAll(slideShowService.getAllSlideshows());
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Slideshows", "Fehlermeldung: " + ex.getMessage());
        }
    }

    private void startSlideshow(Event event) {







    }
    
}
