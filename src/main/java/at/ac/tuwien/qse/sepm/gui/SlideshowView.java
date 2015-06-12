package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class SlideshowView {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ProgressIndicator progress = new ProgressIndicator();
    //private final ImageView imageView = new ImageView();
    private final StackPane container = new StackPane();

    //private final ImageGrid<Photo> grid = new ImageGrid<>(SlideGridTile::new); //TODO:



    private Image image = null;
    private boolean selected = false;
    private Photo item = null;

    @Autowired private SlideService slideService;
    @Autowired private SlideshowService slideShowService;


    @FXML private ImageView imageView = new ImageView();
    @FXML private ScrollPane gridContainer;
    @FXML private Button Btn_Add;
    @FXML private ComboBox cb_getSlideshows;
    @FXML private TextField tf_slideName;

    public SlideshowView() {
        //getStyleClass().add("image-tile");

        progress.setMaxWidth(50);
        progress.setMaxHeight(50);
        //setMargin(progress, new Insets(16));
        //setPadding(new Insets(4));

        imageView.setFitWidth(142);
        imageView.setFitHeight(142);
        imageView.setPreserveRatio(true);


        //super.getChildren().add(progress);
        //super.getChildren().add(imageView);
        //super.getChildren().add(container);
    }

    @FXML
    private void initialize() {

        //gridContainer.setContent(grid);

        //Btn_Add.setOnAction(this::handlesetShowSlides);

        getAllSlideshowsToComboBox();

    }



    public void addPhotoToGrid(List<Photo> photos) {
        //selectedPhotos= photoGrid.getSelectedPhotos();
        LOGGER.debug(photos.get(0).getPath());
        //grid.addItem(photos.get(0)); TODO:
        //LOGGER.debug("Count of Items in Grid"+grid.getItems().size()); TODO:

        setItem(photos.get(0));
        //grid.addItem(photos.get(0));
    }

    //@Override
    public void setItem(Photo photo) {
        //super.setItem(photo);
        if (photo == null) return;
        showImage(photo.getPath());
        //showRating(photo.getRating());
        //showTags(photo.getTags());
        //showDate(photo.getDatetime());
    }

    private void showImage(String path) {
        File file = new File(path);
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException ex) {
            LOGGER.error("photo URL is malformed", ex);
            // TODO: handle exception
        }
        LOGGER.debug(url);
        loadImage(url);
    }

    protected void loadImage(String url) {
        //indicateLoading();
        //image = new Image(url, imageView.getFitWidth(), imageView.getFitHeight(), false, true, true);
        image = new Image(url);
        imageView.setImage(image);
        /*image.progressProperty().addListener((observable, oldValue, newValue) -> {
            // Image is fully loaded.
            if (newValue.doubleValue() == 1.0) {
                //indicateLoaded();
            }
        });*/
        LOGGER.debug("Done!");
    }

    private void getAllSlideshowsToComboBox() {


        try {
            List<Slideshow> slideshows;
            slideshows= slideShowService.getAllSlideshows();


            for (int i = 0; i < slideshows.size(); i++)
                cb_getSlideshows.getItems().addAll(slideshows.get(i).getName());

        } catch (ServiceException e) {
            e.printStackTrace();
        }
        //ArrayList<Slideshow> p = new ArrayList<Slideshow>(slideshows);
        //ObservableList<Slideshow> observableList1 = FXCollections.observableArrayList(p);

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

    private void handlesetShowSlides(Event event){
        createSlideshow();
    }
    
}
