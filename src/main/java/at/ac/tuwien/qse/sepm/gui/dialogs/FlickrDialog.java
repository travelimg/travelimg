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
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
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
    private ImageTile lastSelected;
    private ArrayList<Photo> photos = new ArrayList<>();
    private Cancelable searchTask;
    private Cancelable downloadTask;
    private Menu sender;
    private boolean newSearch = false;

    public FlickrDialog(Node origin, String title, FlickrService flickrService, ExifService exifService, IOHandler ioHandler,
            Menu sender) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, FlickrDialog.class, "view/FlickrDialog.fxml");

        this.flickrService = flickrService;
        this.exifService = exifService;
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
        cancelButton.setOnAction(e -> handleLeaveFlickrDialog());
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
        } catch (ServiceException e) {
            reActivateElements();
        }
    }

    private void handleFullscreen() {
        FullscreenWindow fullscreenWindow = new FullscreenWindow();
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
            ImageTile imageTile = (ImageTile) photosFlowPane.getChildren().get(i);
            if(imageTile.isSelected()){
                flickrPhotos.add(imageTile.getFlickrPhoto());
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

        try {

            downloadTask = flickrService.downloadPhotos(flickrPhotos,
                    new Consumer<com.flickr4java.flickr.photos.Photo>() {
                        @Override public void accept(com.flickr4java.flickr.photos.Photo flickrPhoto) {
                            Photo p = new Photo();
                            p.setPath(tmpDir+flickrPhoto.getId()+"_o."+flickrPhoto.getOriginalFormat());
                            p.getData().setLatitude(flickrPhoto.getGeoData().getLatitude());
                            p.getData().setLongitude(flickrPhoto.getGeoData().getLongitude());
                            p.getData().setDatetime(datePicker.getValue().atStartOfDay());
                            //TODO set tags
                            //need a setter for that in photo entity
                            //Path path = Paths.get(p.getPath());
                            //TODO method which alters exif data (date, tags, geodata)
                            //exifService.setDateAndGeoData(p);
                            //ioHandler.copyFromTo(Paths.get(p.getPath()),Paths.get(System.getProperty("user.home"),"travelimg/"+path.getFileName()));
                        }
                    }
                    ,
                    new Consumer<Double>() {
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
                    }
                    , new ErrorHandler<ServiceException>() {

                        public void handle(ServiceException exception) {
                            downloadProgressControl.finish(true);
                        }
                    });
        } catch (ServiceException e) {
            downloadProgressControl.finish(true);
        }
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
            imageView.setFitHeight(150);
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

    /**
     * A simple keyword with a close button.
     */
    private class Keyword extends HBox{

        private String name;
        private Text x;

        public Keyword(String name){
            super();
            this.name = name;
            setStyle("-fx-background-radius: 5; -fx-background-color: -tmg-primary; ");
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

    /**
     * A special popup control showing progress using a progressbar.
     */
    private class DownloadProgressControl extends PopupControl{

        private final BorderPane borderPane = new BorderPane();
        private final ProgressBar progressBar = new ProgressBar();
        private Button button;
        private FadeTransition ft;
        private String buttonStyle;
        private Tooltip buttonTooltip;
        private EventHandler buttonOnAction;

        public DownloadProgressControl(Button button){
            super();
            this.button = button;
            this.buttonStyle = button.getGraphic().getStyle();
            this.buttonTooltip = button.getTooltip();
            this.buttonOnAction = button.getOnAction();
            this.ft = new FadeTransition(Duration.millis(1000), button);

            ft.setFromValue(1.0);
            ft.setToValue(0.3);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setAutoReverse(false);
            ft.play();

            Label label = new Label("Fotos werden heruntergeladen");
            label.setAlignment(Pos.CENTER);

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(5.0,5.0,5.0,5.0));
            hBox.getChildren().add(progressBar);
            progressBar.setProgress(0.0);

            borderPane.setStyle("-fx-background-color: #f5f5b5; -fx-background-radius: 5.0; -fx-border-radius: 5.0; -fx-border-color: black; -fx-border-width: 0.5;");
            borderPane.setPadding(new Insets(2.0, 2.0, 2.0, 2.0));
            borderPane.setCenter(label);
            borderPane.setBottom(hBox);
            getScene().setRoot(borderPane);

            button.setOnMouseEntered(event -> handleShow());
            button.setOnMouseExited(event -> fadeOut());
        }

        public void setProgress(double progress){
            progressBar.setProgress(progress);
        }

        public void finish(boolean interrupted){
            ft.stop();
            borderPane.getChildren().clear();

            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5.0,5.0,5.0,5.0));
            hBox.setAlignment(Pos.CENTER);
            if(!interrupted){
                hBox.getChildren().add(new Label("Fotos heruntergeladen "));
                hBox.getChildren().add(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
            }
            else{
                hBox.getChildren().add(new Label("Herunterladen fehlgeschlagen "));
                hBox.getChildren().add(new FontAwesomeIconView(FontAwesomeIcon.TIMES));
            }

            borderPane.setCenter(hBox);

            button.setOnMouseExited(e -> {
                fadeOut();
                button.setOnMouseEntered(null);
                button.setOnMouseExited(null);
                button.setTooltip(buttonTooltip);
                button.getGraphic().setStyle(buttonStyle);
            });

            button.setOnAction(buttonOnAction);
        }

        private void handleShow(){
            Point2D p = button.localToScene(button.getLayoutBounds().getMinX(), button.getLayoutBounds().getMinY());
            show(button,p.getX() + button.getScene().getX() + button.getScene().getWindow().getX()+55.0,p.getY() + button.getScene().getY() + button.getScene().getWindow().getY()-35.0);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), borderPane);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();
        }

        private void fadeOut(){
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), borderPane);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.play();
        }

    }
}
