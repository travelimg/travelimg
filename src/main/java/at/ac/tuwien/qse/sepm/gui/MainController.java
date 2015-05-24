package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.PhotoServiceImpl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.Event;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the main view.
 */
public class MainController {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private Organizer organizer;
    @Autowired private Inspector inspector;
    @Autowired private PhotoService photoService;

    @FXML private ScrollPane scrollPane;

    @FXML private TilePane tilePane;
    @FXML private Insets in;
    @FXML private BorderPane root;
    @FXML private Tab gitter;
    @FXML private Tab weltkarte;
    @FXML private Tab timeline;
    @FXML private TabPane tabs;
    SelectionModel<Tab> selectionMod;
    ChangeListener<SelectionModel<Tab>> changer;
    private GoogleMapsScene worldMap;
    private ImageTile selectedTile = null;
    private boolean gitterSelect =false;
    @FXML private FlowPane flowPane;
    private ArrayList<ImageTile> selectedImages = new ArrayList<ImageTile>();

    private List<Photo> activePhotos = new ArrayList<>();


    public MainController() {


    }



    @FXML
    private void initialize() {

        worldMap = new GoogleMapsScene(getAllPhotos());
        weltkarte.setContent(worldMap.getMapView());
        gitter.setContent(scrollPane);

        root.getCenter().setOnMouseClicked(this::handleClick);

        scrollPane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.isControlDown() && event.getCode()== KeyCode.A){
                    for (Node n : flowPane.getChildren()) {
                        if(n instanceof ImageTile){
                            if(!((ImageTile) n).getSelectedProperty().getValue()){
                                ((ImageTile) n).select();
                                selectedImages.add((ImageTile) n);
                            }
                        }
                    }
                }
            }
        });
    }

    private void handleClick(MouseEvent mouseEvent) {
       if(gitter.isSelected()) {
            gitterSelect = true;
       }
           if(weltkarte.isSelected() && gitterSelect){
               gitterSelect=false;
               inspector.disableDetails();

               worldMap = new GoogleMapsScene(getAllPhotos());
               weltkarte.setContent(worldMap.getMapView());
           }
    }



    /**
     * returns a ArrayList with all Photos
     * @return ArrayList with all Photos
     */
    private ArrayList<Photo> getAllPhotos(){
        ArrayList<Photo> l = new ArrayList<>();
        try {

            for(Photo p : photoService.getAllPhotos()){
                l.add(p);
            }

        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * Add a photo to the image grid
     *
     * @param photo The photo to be added.
     */
    public void addPhoto(Photo photo) {
        ImageTile imageTile = new ImageTile(photo);
        activePhotos.add(photo);

        imageTile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if (event.isControlDown()) {
                    if (imageTile.getSelectedProperty().getValue()) {
                        imageTile.unselect();
                        selectedImages.remove(imageTile);
                    } else {
                        imageTile.select();
                        selectedImages.add(imageTile);
                        imageTile.getSelectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override public void changed(
                                    ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                    Boolean newValue) {
                                if (newValue) {
                                    if (selectedTile != null) {
                                        selectedTile.unselect();
                                    }
                                } else {
                                    for (ImageTile i : selectedImages) {
                                        i.unselect();
                                    }
                                    selectedImages.clear();
                                    imageTile.select();
                                    selectedImages.add(imageTile);
                                }
                            }
                        });

                        tilePane.getChildren().add(imageTile);

                        flowPane.getChildren().add(imageTile);
                    }
                }
            }
        });

    }



                    /**
                     * Return the List of active photos.
                     *
                     * @return the currently active photos
                     */
                    public List<Photo> getActivePhotos () {
                        return activePhotos;
                    }
                    /**
                     * Clear the image grid and don't show any photos.
                     * /
                     *
                     **/

                public void clearPhotos () {
                    activePhotos.clear();
                    flowPane.getChildren().clear();
                }

                public void deletePhotos () {
                    for (ImageTile i : selectedImages) {
                        flowPane.getChildren().remove(i);
                    }
                    selectedImages.clear();
                }

                /**
                 * Widget for one widget in the image grid. Can either be in a selected or an unselected state.
                 */
                private class ImageTile extends HBox {

                    private Photo photo;

                    private Image image;
                    private ImageView imageView;

                    private BooleanProperty selected = new SimpleBooleanProperty(false);

                    public ImageTile(Photo photo) {
                        this.photo = photo;

                        try {
                            image = new Image(new FileInputStream(new File(photo.getPath())), 150,
                                    0, true, true);
                        } catch (FileNotFoundException ex) {
                            logger.error("Could not find photo", ex);
                            return;
                        }

                        imageView = new ImageView(image);
                        imageView.setFitWidth(150);
                        imageView.setOnMouseClicked(this::handleSelected);

                        getStyleClass().add("image-tile-non-selected");

                        this.getChildren().add(imageView);
                    }

                    /**
                     * Select this photo. Triggers an update of the inspector widget.
                     */
                    public void select() {
                        getStyleClass().remove("image-tile-non-selected");
                        getStyleClass().add("image-tile-selected");
                        inspector.setMap(worldMap);
                        inspector.addActivePhoto(photo);
                        this.selected.set(true);
                    }

                    /**
                     * Unselect a photo.
                     */
                    public void unselect() {
                        getStyleClass().add("image-tile-non-selected");
                        getStyleClass().remove("image-tile-selected");
                        getStyleClass().add("image-tile-non-selected");
                        inspector.removeActivePhoto(photo);
                        this.selected.set(false);
                    }

                    /**
                     * Property which represents if this tile is currently selected or not.
                     *
                     * @return The selected property.
                     */
                    public BooleanProperty getSelectedProperty() {
                        return selected;
                    }

                    private void handleSelected(MouseEvent event) {
                        select();
                    }
                }

            }



