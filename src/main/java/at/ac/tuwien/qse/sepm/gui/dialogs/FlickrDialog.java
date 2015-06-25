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
import java.util.Optional;
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
    private ArrayList<ImageTile> selectedImages = new ArrayList<>();
    private ArrayList<Photo> photos = new ArrayList<>();
    private Cancelable sarchTask;

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
                            if (!((ImageTile) n).getSelectedProperty().getValue()) {
                                ((ImageTile) n).select();
                                selectedImages.add((ImageTile) n);
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
        resetButton.setOnAction(e -> handleReset());
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
        ObservableList<Node> test = keywordsFlowPane.getChildren();
        String tags[] = new String[test.size()];
        for (int i = 0; i < test.size(); i++) {
            HBox h = (HBox) test.get(i);
            tags[i] = ((Text) h.getChildren().get(0)).getText();
        }

        try {
            double latitude = 0.0;
            double longitude = 0.0;
            boolean useGeoData = false;

            if (actualLatLong != null) {
                latitude = actualLatLong.getLatitude();
                longitude = actualLatLong.getLongitude();
                useGeoData = true;
            }
            sarchTask = flickrService.searchPhotos(tags, latitude, longitude, useGeoData,
                    new Consumer<com.flickr4java.flickr.photos.Photo>() {

                        public void accept(com.flickr4java.flickr.photos.Photo photo) {

                            Platform.runLater(new Runnable() {

                                public void run() {
                                    ImageTile imageTile = new ImageTile(photo);
                                    Photo p = new Photo();
                                    p.setPath(tmpDir+photo.getId()+"."+photo.getOriginalFormat());
                                    photos.add(p);
                                    imageTile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override public void handle(MouseEvent event) {
                                            if (event.isControlDown()) {
                                                if (imageTile.getSelectedProperty().getValue()) {
                                                    imageTile.unselect();
                                                    selectedImages.remove(imageTile);
                                                } else {
                                                    imageTile.select();
                                                    selectedImages.add(imageTile);
                                                }
                                            } else {
                                                for (ImageTile i : selectedImages) {
                                                    i.unselect();
                                                }
                                                selectedImages.clear();
                                                imageTile.select();
                                                selectedImages.add(imageTile);
                                            }

                                            if (selectedImages.isEmpty()) {
                                                importButton.setDisable(true);
                                            } else {
                                                importButton.setDisable(false);
                                            }
                                        }
                                    });

                                    photosFlowPane.getChildren().add(imageTile);
                                }
                            });

                        }

                    }, new Consumer<Double>() {

                        public void accept(Double downloadProgress) {

                            Platform.runLater(new Runnable() {

                                public void run() {
                                    if (downloadProgress == 1.0) {
                                        searchButton.setDisable(false);
                                        progressIndicator.setVisible(false);
                                    }
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
            e.printStackTrace();
        }
    }

    private void handleFullscreen() {
        FullscreenWindow fullscreenWindow = new FullscreenWindow();
        fullscreenWindow.present(photos,photos.get(0));
    }

    private void handleReset(){
        selectedImages.clear();
        photos.clear();
        photosFlowPane.getChildren().clear();
        actualLatLong = null;
        mapScene.clear();
        keywordsFlowPane.getChildren().clear();
        keywordTextField.clear();
        fullscreenButton.setDisable(true);
        importButton.setDisable(true);
        if (sarchTask != null)
            sarchTask.cancel();
    }

    private void handleImport() {
        flickrService.reset();
        if(selectedImages.isEmpty()){
            return;
        }
        ArrayList<com.flickr4java.flickr.photos.Photo> photos = new ArrayList<>();
        for (ImageTile i : selectedImages) {
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
        selectedImages.clear();
        close();
    }

    private void handleStop() {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(borderPane, "Download abbrechen", "Download abbrechen?");
        Optional<Boolean> confirmed = confirmationDialog.showForResult();
        if (!confirmed.isPresent() || !confirmed.get()) return;
        logger.debug("Canceling the download...");
        if (sarchTask != null)
            sarchTask.cancel();

    }

    /**
     * Widget for one widget in the image grid. Can either be in a selected or an unselected state.
     */
    private class ImageTile extends HBox {

        private BooleanProperty selected = new SimpleBooleanProperty(false);
        private com.flickr4java.flickr.photos.Photo photo;
        private Image image;
        private ImageView imageView;
        private Photo p;

        public ImageTile(com.flickr4java.flickr.photos.Photo photo) {

            this.photo = photo;
            try {
                image = new Image(new FileInputStream(new File(tmpDir+ photo.getId()+"."+ photo
                        .getOriginalFormat())), 150, 0, true, true);
            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }

            imageView = new ImageView(image);
            imageView.setFitWidth(150);

            getStyleClass().add("image-tile-non-selected");


            this.getChildren().add(imageView);
        }

        public void setPhoto(Photo p){
            this.p=p;
        }

        public Photo getPhoto(){
            return p;
        }

        /**
         * Select this photo. Triggers an update of the inspector widget.
         */
        public void select() {
            setStyle("-fx-effect: dropshadow(three-pass-box, black, 5, 5, 0, 0);");
            this.selected.set(true);
        }

        /**
         * Unselect a photo.
         */
        public void unselect() {
            setStyle("-fx-effect: null");
            this.selected.set(false);
        }

        public com.flickr4java.flickr.photos.Photo getFlickrPhoto() {
            return photo;
        }

        /**
         * Property which represents if this tile is currently selected or not.
         *
         * @return The selected property.
         */
        public BooleanProperty getSelectedProperty() {
            return selected;
        }
    }

    private class Keyword extends HBox{

        private Text x;

        public Keyword(String name){
            super();
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
