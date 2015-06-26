package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import at.ac.tuwien.qse.sepm.gui.controller.impl.MenuImpl;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FlickrDialog extends ResultDialog<List<com.flickr4java.flickr.photos.Photo>> {

    @FXML
    private BorderPane borderPane;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private FlowPane photosFlowPane;
    @FXML
    private Button searchButton, fullscreenButton, resetButton, importButton, cancelButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane keywordsFlowPane;
    @FXML
    private TextField keywordTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private GoogleMapScene mapScene;

    private static final Logger logger = LogManager.getLogger();
    private static final String tmpDir = System.getProperty("java.io.tmpdir");
    private FlickrService flickrService;
    private ExifService exifService;
    private IOHandler ioHandler;
    private LatLong actualLatLong;
    private ArrayList<ImageTile> selectedImageTiles = new ArrayList<>();
    private ImageTile lastSelected;
    private ArrayList<Photo> photos = new ArrayList<>();
    private Cancelable searchTask;
    private Menu sender;

    public FlickrDialog(Node origin, String title, FlickrService flickrService, ExifService exifService, IOHandler ioHandler,
            Menu sender) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, FlickrDialog.class, "view/FlickrDialog.fxml");

        this.flickrService = flickrService;
        this.exifService = exifService;
        this.ioHandler = ioHandler;
        this.sender = sender;

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
                if (keywordTextField.getText().length() > 20) {
                    String s = keywordTextField.getText().substring(0, 20);
                    keywordTextField.setText(s);
                }
            }
        });
        scrollPane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown() && event.getCode() == KeyCode.A) {
                    for (Node n : photosFlowPane.getChildren()) {
                        if (n instanceof ImageTile) {
                            ((ImageTile) n).select();
                        }
                    }
                }
            }
        });
        scrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if (!event.isControlDown()) {
                    for (Node n : photosFlowPane.getChildren()) {
                        if(n instanceof ImageTile){
                            if(n==lastSelected) {
                                ((ImageTile) n).select();
                            }
                            else {
                                ((ImageTile) n).deselect();
                            }
                        }
                    }
                }
            }
        });

        datePicker.setValue(LocalDate.now());
        datePicker.setTooltip(new Tooltip("Wählen Sie ein Datum für die Fotos aus"));
        mapScene.setDoubleClickCallback((position) -> dropMarker(position));
        cancelButton.setOnAction(e -> close());
        importButton.setOnAction(e -> handleImport());
        searchButton.setOnAction(e -> handleSearch());
        fullscreenButton.setOnAction(e -> handleFullscreen());
        resetButton.setOnAction(e -> handleStop());
    }

    private void dropMarker(LatLong ll) {
        if (actualLatLong != null) {
            mapScene.clear();
        }
        this.actualLatLong = ll;
        mapScene.addMarker(ll);
        flickrService.reset();
    }

    public void addKeyword(String name) {
        Keyword keyword = new Keyword(name);
        keyword.setOnClosed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                keywordsFlowPane.getChildren().remove(keyword);
                flickrService.reset();
            }
        });
        keywordsFlowPane.getChildren().add(keyword);
        flickrService.reset();
    }

    private void handleSearch() {
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

        try {
            searchTask = flickrService.searchPhotos(tags, latitude, longitude, useGeoData

                    , new Consumer<com.flickr4java.flickr.photos.Photo>() {
                        public void accept(com.flickr4java.flickr.photos.Photo photo) {
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    ImageTile imageTile = new ImageTile(photo);
                                    Photo p = new Photo();
                                    p.setPath(tmpDir+photo.getId()+"."+photo.getOriginalFormat());
                                    photos.add(p);
                                    if(photos.size()==1){
                                        fullscreenButton.setDisable(false);
                                    }
                                    photosFlowPane.getChildren().add(imageTile);
                                }
                            });
                        }
                    }
                    , new Consumer<Double>() {
                        public void accept(Double downloadProgress) {
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    if (downloadProgress == 1.0) {
                                        searchButton.setDisable(false);
                                        progressIndicator.setVisible(false);
                                        importButton.setDisable(false);
                                    }
                                }
                            });
                        }
                    }
                    , new ErrorHandler<ServiceException>() {
                        public void handle(ServiceException exception) {
                            searchButton.setDisable(false);
                            progressIndicator.setVisible(false);
                            importButton.setDisable(false);
                        }
                    });
        } catch (ServiceException e) {
            searchButton.setDisable(false);
            progressIndicator.setVisible(false);
            importButton.setDisable(false);
        }
    }

    private void handleFullscreen() {
        FullscreenWindow fullscreenWindow = new FullscreenWindow();
        fullscreenWindow.present(photos, photos.get(0));
    }

    private void handleReset(){
        flickrService.reset();
        selectedImageTiles.clear();
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
        if(selectedImageTiles.isEmpty()){
            return;
        }
        ArrayList<com.flickr4java.flickr.photos.Photo> photos = new ArrayList<>();
        for (ImageTile i : selectedImageTiles) {
             //i.getFlickrPhoto().setDateAdded(datePicker.getValue().atStartOfDay());
            photos.add(i.getFlickrPhoto());
            //exifService.setDateAndGeoData(p);
            //Path path = Paths.get(p.getPath());
            //ioHandler.copyFromTo(Paths.get(p.getPath()),Paths.get(System.getProperty("user.home"),"travelimg/"+path.getFileName()));
            logger.debug("Added photo for import {}", i.getFlickrPhoto());
        }
        Button flickrButton = ((MenuImpl)sender).getFlickrButton();
        String flickrButtonStyle = flickrButton.getGraphic().getStyle();
        flickrButton.getGraphic().setStyle("-fx-fill: -tmg-primary;");
        Tooltip flickrButtonTooltip = flickrButton.getTooltip();
        flickrButton.setTooltip(null);
        EventHandler flickrButtonOnAction = flickrButton.getOnAction();
        flickrButton.setOnAction(null);

        try {

            DownloadProgressControl downloadProgressControl = new DownloadProgressControl(flickrButton);

            flickrService.downloadPhotos(photos, new Consumer<Double>() {
                public void accept(Double downloadProgress) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            downloadProgressControl.setProgress(downloadProgress);
                            if (downloadProgress == 1.0) {
                                downloadProgressControl.finish();
                                flickrButton.getGraphic().setStyle(flickrButtonStyle);
                                flickrButton.setTooltip(flickrButtonTooltip);
                                flickrButton.setOnAction(flickrButtonOnAction);
                            }
                            logger.debug("Downloading photos from flickr. Progress {}",
                                    downloadProgress);
                        }
                    });

                }
            }

                    , new ErrorHandler<ServiceException>() {

                public void handle(ServiceException exception) {
                    //handle errors here
                }
                    });
        } catch (ServiceException e) {

        }
        selectedImageTiles.clear();
        close();
    }

    private void handleStop() {
        //ConfirmationDialog confirmationDialog = new ConfirmationDialog(borderPane, "Download abbrechen", "Download abbrechen?");
        //Optional<Boolean> confirmed = confirmationDialog.showForResult();
        //if (!confirmed.isPresent() || !confirmed.get()) return;
        handleReset();
        close();
    }

    /**
     * Widget for one widget in the image grid. Can either be in a selected or an unselected state.
     */
    private class ImageTile extends StackPane {

        private final BorderPane overlay = new BorderPane();
        private BooleanProperty selected = new SimpleBooleanProperty(false);
        private com.flickr4java.flickr.photos.Photo flickrPhoto;

        public ImageTile(com.flickr4java.flickr.photos.Photo flickrPhoto) {
            super();
            this.flickrPhoto = flickrPhoto;
            Image image = null;
            try {
                image = new Image(new FileInputStream(new File(tmpDir+ flickrPhoto.getId()+"."+ flickrPhoto
                        .getOriginalFormat())), 150, 0, true, true);
            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            getChildren().add(imageView);

            setAlignment(overlay,Pos.BOTTOM_CENTER);
            FontAwesomeIconView checkIcon = new FontAwesomeIconView();
            checkIcon.setGlyphName("CHECK");
            checkIcon.setStyle("-fx-fill: white");
            overlay.setAlignment(checkIcon, Pos.CENTER_RIGHT);
            overlay.setStyle("-fx-background-color: -tmg-secondary; -fx-max-height: 20px;");
            overlay.setBottom(checkIcon);
            getChildren().add(overlay);
            overlay.setVisible(false);
            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    if(!getSelectedProperty().getValue()){
                        select();
                    }
                    else{
                        deselect();
                    }
                }
            });
        }

        public com.flickr4java.flickr.photos.Photo getFlickrPhoto() {
            return flickrPhoto;
        }

        public void select() {
            selected.setValue(true);
            overlay.setVisible(true);
            setStyle("-fx-border-color: -tmg-secondary");
            lastSelected = this;
        }

        public void deselect() {
            selected.setValue(false);
            overlay.setVisible(false);
            setStyle("-fx-border-color: none");
        }

        /**
         * Property which represents if this tile is currently selected or not.
         *
         * @return The selected property.
         */
        public BooleanProperty getSelectedProperty() {
            return selected;
        }

        public boolean isSelected() {
            return selected.getValue();
        }

    }

    private class Keyword extends HBox{

        private String name;
        private Text x;

        public Keyword(String name){
            super();
            this.name = name;
            setStyle("-fx-background-radius: 5; -fx-background-color: #E91E63; ");
            Text text = new Text(name);
            text.setFill(Color.WHITE);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(3, 5, 5, 5));
            getChildren().add(text);
            this.x = new Text("x");
            x.setOnMouseEntered(event -> keywordsFlowPane.setCursor(Cursor.HAND));
            x.setOnMouseExited(event -> keywordsFlowPane.setCursor(Cursor.DEFAULT));
            getChildren().add(new Text("  "));
            getChildren().add(x);
        }

        public String getName() {
            return name;
        }

        public void setOnClosed(EventHandler eventHandler){
            x.setOnMouseClicked(eventHandler);
        }

    }

    private class DownloadProgressControl extends PopupControl{

        private BorderPane borderPane;
        private Button button;
        private ProgressBar progressBar;
        private FadeTransition ft;

        public DownloadProgressControl(Button button){
            super();
            this.borderPane = new BorderPane();
            this.button = button;
            this.progressBar = new ProgressBar();

            this.ft = new FadeTransition(Duration.millis(1000), button);
            ft.setFromValue(1.0);
            ft.setToValue(0.3);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setAutoReverse(true);
            ft.play();
            progressBar.setProgress(0.0);
            FontAwesomeIconView stopIcon = new FontAwesomeIconView();
            stopIcon.setGlyphName("TIMES");
            HBox hBox = new HBox(5.0);
            hBox.setAlignment(Pos.CENTER);

            hBox.setPadding(new Insets(5.0,5.0,5.0,5.0));
            hBox.getChildren().add(progressBar);
            hBox.getChildren().add(stopIcon);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView();
            minimizeIcon.setGlyphName("MINUS");
            borderPane.setStyle("-fx-background-color: white");
            borderPane.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            borderPane.setRight(minimizeIcon);
            Label label = new Label("Fotos werden heruntergeladen");
            label.setAlignment(Pos.CENTER);
            borderPane.setCenter(label);
            borderPane.setBottom(hBox);

            minimizeIcon.setOnMouseClicked(event -> hide());
            Point2D p = button.localToScene(button.getLayoutBounds().getMinX(), button.getLayoutBounds().getMinY());
            //double posX = p.getX() + flickrButton.getScene().getX() + flickrButton.getScene().getWindow().getX();
            //double posY = p.getY() + flickrButton.getScene().getY() + flickrButton.getScene().getWindow().getY();
            button.setOnMouseEntered(event -> show(button,p.getX()+35.0,p.getY()-35.0));
            getScene().setRoot(borderPane);
        }

        public void setProgress(double progress){
            progressBar.setProgress(progress);
        }

        public void finish(){
            ft.stop();
            borderPane.getChildren().clear();
            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5.0,5.0,5.0,5.0));
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().add(new Label("Fotos wurden heruntergeladen"));
            FontAwesomeIconView checkIcon = new FontAwesomeIconView();
            checkIcon.setGlyphName("CHECK");
            hBox.getChildren().add(checkIcon);
            FontAwesomeIconView closeIcon = new FontAwesomeIconView();
            closeIcon.setGlyphName("CLOSE");
            closeIcon.setOnMouseClicked(event -> hide());
            button.setOnMouseEntered(null);
            button.setOnMouseExited(null);
            borderPane.setCenter(hBox);
            borderPane.setRight(closeIcon);
            Point2D p = button.localToScene(button.getLayoutBounds().getMinX(), button.getLayoutBounds().getMinY());
            hide();
            show(button,p.getX()+35.0,p.getY()-5.0);
        }

    }
}
