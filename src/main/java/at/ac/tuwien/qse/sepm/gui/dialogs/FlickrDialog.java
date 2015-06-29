package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.control.*;
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import at.ac.tuwien.qse.sepm.gui.controller.impl.MenuImpl;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import com.flickr4java.flickr.tags.Tag;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class FlickrDialog extends ResultDialog<List<com.flickr4java.flickr.photos.Photo>> {

    @FXML
    private ScrollPane photosScrollpane;
    @FXML
    private FlowPane photosFlowPane;
    @FXML
    private GoogleMapScene mapScene;
    @FXML
    private FlowPane keywordsFlowPane;
    @FXML
    private TextField keywordTextField;
    @FXML
    private Button searchButton, fullscreenButton, resetButton, importButton, cancelButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private FlickrJourneysComboBox journeysComboBox;
    @FXML
    private FlickrDatePicker flickrDatePicker;

    private static final Logger logger = LogManager.getLogger();
    private static final String tmpDir = System.getProperty("java.io.tmpdir");
    private FlickrService flickrService;
    private ExifService exifService;
    private ClusterService clusterService;
    private PhotoService photoService;
    private IOHandler ioHandler;
    private LatLong actualLatLong;
    private ArrayList<Photo> photos = new ArrayList<>();
    private Cancelable searchTask;
    private Menu sender;
    private boolean newSearch = false;

    public FlickrDialog(Node origin, String title, FlickrService flickrService, ExifService exifService,
            ClusterService clusterService, PhotoService photoService, IOHandler ioHandler,
            Menu sender) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, FlickrDialog.class, "view/FlickrDialog.fxml");

        this.flickrService = flickrService;
        this.exifService = exifService;
        this.clusterService = clusterService;
        this.photoService = photoService;
        this.ioHandler = ioHandler;
        this.sender = sender;

        flickrService.reset();

        keywordTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER && !keywordTextField.getText().isEmpty()) {
                    addKeyword(keywordTextField.getText().trim());
                    keywordTextField.clear();
                    keywordsFlowPane.requestFocus();
                }
            }
        });
        keywordTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (keywordTextField.getText().length() > 25) {
                    String s = keywordTextField.getText().substring(0, 25);
                    keywordTextField.setText(s);
                }
            }
        });
        photosScrollpane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                if (event.isControlDown() && event.getCode() == KeyCode.A) {
                    for (Node n : photosFlowPane.getChildren()) {
                        if (n instanceof FlickrImageTile) {
                            ((FlickrImageTile) n).select();
                        }
                    }
                }
            }
        });
        photosScrollpane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if (!event.isControlDown()) {
                    FlickrImageTile selected = null;
                    Object target = event.getTarget();
                    if (target instanceof ImageView) {
                        selected = (FlickrImageTile) ((ImageView) target).getParent();
                    }
                    for (Node n : photosFlowPane.getChildren()) {
                        if (n != selected) {
                            ((FlickrImageTile) n).deselect();
                        } else {
                            ((FlickrImageTile) n).select();
                        }
                    }
                }
            }
        });
        journeysComboBox.bindToFlickrDatePicker(flickrDatePicker);
        mapScene.setClickCallback((position) -> dropMarker(position));
        cancelButton.setOnAction(e -> handleLeaveFlickrDialog());
        importButton.setOnAction(e -> handleImport());
        searchButton.setOnAction(e -> handleSearch());
        fullscreenButton.setOnAction(e -> handleFullscreen());
        resetButton.setOnAction(e -> handleReset());
        try {
            Journey j = new Journey(-1,"Keine Reise",null,null);
            List<Journey> journeys = clusterService.getAllJourneys();
            journeysComboBox.getItems().add(j);
            journeysComboBox.getItems().addAll(journeys);
            journeysComboBox.getSelectionModel().selectFirst();
        } catch (ServiceException e) {
            logger.debug(e);
        }
    }

    private void dropMarker(LatLong ll) {
        if (actualLatLong != null) {
            mapScene.clear();
        }
        this.actualLatLong = ll;
        mapScene.addMarker(ll);
        newSearch = true;
    }

    public void addKeyword(String name) {
        Keyword keyword = new Keyword(name);
        keyword.setOnClosed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                keywordsFlowPane.getChildren().remove(keyword);
                newSearch = true;
            }
        });
        keywordsFlowPane.getChildren().add(keyword);
        newSearch = true;
    }

    private void handleSearch() {
        if(newSearch){
            flickrService.reset();
        }
        newSearch = false;
        searchButton.setDisable(true);
        progressIndicator.setVisible(true);
        importButton.setDisable(true);

        ObservableList<Node> keywords = keywordsFlowPane.getChildren();
        String[] tags = keywords.stream().map(keyword -> ((Keyword)keyword).getName()).toArray(String[]::new);
        double latitude = 0.0;
        double longitude = 0.0;
        boolean useGeoData = false;
        if (actualLatLong != null) {
            latitude = actualLatLong.getLatitude();
            longitude = actualLatLong.getLongitude();
            useGeoData = true;
        }


        searchTask = flickrService.searchPhotos(tags, latitude, longitude, useGeoData

                , new Consumer<com.flickr4java.flickr.photos.Photo>() {
            public void accept(com.flickr4java.flickr.photos.Photo photo) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        FlickrImageTile flickrImageTile = new FlickrImageTile(photo);
                        Photo p = new Photo();

                        p.setPath(Paths.get(tmpDir, photo.getId() + "." + photo.getOriginalFormat()).toString());
                        photos.add(p);
                        if(photos.size()==1){
                            fullscreenButton.setDisable(false);
                        }
                        photosFlowPane.getChildren().add(flickrImageTile);
                    }
                });
            }
        }
                , new Consumer<Double>() {
            public void accept(Double downloadProgress) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        if (downloadProgress == 1.0) {
                            reActivateElements();
                        }
                    }
                });
            }
        }
                , new ErrorHandler<ServiceException>() {
            public void handle(ServiceException exception) {
                reActivateElements();
            }
        });

    }

    private void handleFullscreen() {
        FullscreenWindow fullscreenWindow = new FullscreenWindow(photoService);
        fullscreenWindow.setElementsVisible(false);
        fullscreenWindow.present(photos, photos.get(0));
    }

    private void handleReset(){
        flickrService.reset();
        photos.clear();
        photosFlowPane.getChildren().clear();
        actualLatLong = null;
        mapScene.clear();
        keywordsFlowPane.getChildren().clear();
        keywordTextField.clear();
        fullscreenButton.setDisable(true);
        progressIndicator.setVisible(false);
        if (searchTask != null){
            searchTask.cancel();
            logger.debug("Canceling the download...");
        }
    }

    private void handleImport() {
        flickrService.reset();
        ArrayList<com.flickr4java.flickr.photos.Photo> flickrPhotos = new ArrayList<>();
        for (int i = 0; i<photosFlowPane.getChildren().size(); i++) {
            FlickrImageTile flickrImageTile = (FlickrImageTile) photosFlowPane.getChildren().get(i);
            if(flickrImageTile.isSelected()){
                flickrPhotos.add(flickrImageTile.getFlickrPhoto());
            }
        }
        if(flickrPhotos.isEmpty()){
            logger.debug("No photos selected");
            close();
            return;
        }
        logger.debug("Photos prepared for download {}", flickrPhotos);
        Button flickrButton = ((MenuImpl)sender).getFlickrButton();
        DownloadProgressControl downloadProgressControl = new DownloadProgressControl(flickrButton);
        flickrButton.getGraphic().setStyle("-fx-fill: -tmg-primary;");
        flickrButton.setTooltip(null);
        flickrButton.setOnAction(null);

        flickrService.downloadPhotos(flickrPhotos,
                new Consumer<com.flickr4java.flickr.photos.Photo>() {
                    @Override public void accept(
                            com.flickr4java.flickr.photos.Photo flickrPhoto) {
                        Photo p = new Photo();
                        p.setPath(Paths.get(tmpDir, flickrPhoto.getId() +"_o." + flickrPhoto
                                .getOriginalFormat()).toString());
                        p.getData().setLatitude(flickrPhoto.getGeoData().getLatitude());
                        p.getData().setLongitude(flickrPhoto.getGeoData().getLongitude());
                        p.getData().setDatetime(flickrDatePicker.getValue().atStartOfDay());
                        HashSet<at.ac.tuwien.qse.sepm.entities.Tag> tags = new HashSet<>();
                        for (Tag t : flickrPhoto.getTags()) {
                            tags.add(new at.ac.tuwien.qse.sepm.entities.Tag(0, t.getValue()));
                        }
                        p.getData().setTags(tags);
                        try {
                            p.getData().setPhotographer(new Photographer(2,"Flickr"));
                            Journey selectedJourney = journeysComboBox.getSelectedJourney();
                            if (selectedJourney.getId() != -1) {
                                p.getData().setJourney(selectedJourney);
                                Place place = clusterService.getPlaceNearTo(p);
                                p.getData().setPlace(place);
                            }
                            logger.debug("Photo {} ready for the exifservice.",p);

                            exifService.setMetaData(p);

                            ioHandler.copyFromTo(Paths.get(p.getPath()),
                                    Paths.get(System.getProperty("user.home"),
                                            "travelimg/" + flickrPhoto.getId() + "."
                                                    + flickrPhoto.getOriginalFormat()));
                        } catch (ServiceException | IOException e) {
                            logger.debug(e);
                        }
                    }
                }, new Consumer<Double>() {
                    public void accept(Double downloadProgress) {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                downloadProgressControl.setProgress(downloadProgress);
                                if (downloadProgress == 1.0) {
                                    downloadProgressControl.finish(false);
                                }
                                logger.debug("Downloading photos from flickr. Progress {}",
                                        downloadProgress);
                            }
                        });

                    }
                }, new ErrorHandler<ServiceException>() {

                    public void handle(ServiceException exception) {
                        downloadProgressControl.finish(true);
                    }
                });

        close();
    }

    private void handleLeaveFlickrDialog() {
        handleReset();
        close();
    }

    private void reActivateElements(){
        searchButton.setDisable(false);
        progressIndicator.setVisible(false);
        importButton.setDisable(false);
    }
}