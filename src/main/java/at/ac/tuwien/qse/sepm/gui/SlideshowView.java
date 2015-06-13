package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.grid.SlideshowGrid;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
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

    @FXML private BorderPane root;
    @FXML private ScrollPane gridContainer;
    @FXML private Button Btn_Add;
    @FXML private ComboBox cb_getSlideshows;
    @FXML private TextField tf_slideName;

    @Autowired
    private SlideshowOrganizer slideshowOrganizer;

    private SlideshowGrid grid = null;

    public SlideshowView() {

    }

    @Autowired
    public void setImageCache(ImageCache imageCache) {
        if (grid == null) {
            this.grid = new SlideshowGrid(imageCache);
        }
    }

    @FXML
    private void initialize() {
        gridContainer.setContent(grid);

        Btn_Add.setOnAction(this::handlesetShowSlides);

        getAllSlideshowsToComboBox();

        slideshowOrganizer.getSelectedSlideshowProperty().addListener((observable, oldValue, newValue) -> {
            grid.setSlideshow(newValue);
        });

    }

    private void getAllSlideshowsToComboBox() {
        try {
            List<Slideshow> slideshows;
            slideshows = slideShowService.getAllSlideshows();

            for (int i = 0; i < slideshows.size(); i++)
                cb_getSlideshows.getItems().addAll(slideshows.get(i).getName());

        } catch (ServiceException e) {
            e.printStackTrace();
        }
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

    public void onSlidesAdded(Slideshow slideshow, List<Slide> slides) {
        LOGGER.debug("Implement me");
    }
    
}
