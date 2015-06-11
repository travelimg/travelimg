package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.GoogleMapsScene;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import netscape.javascript.JSObject;
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

public class FlickrDialog extends ResultDialog<List<Photo>> {

    private static final Logger logger = LogManager.getLogger();
    @FXML
    private BorderPane borderPane;
    @FXML
    private HBox progress;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private FlowPane photosFlowPane;
    @FXML
    private Button downloadButton, importButton, stopButton;
    @FXML
    private Pane mapContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane keywordsFlowPane;
    @FXML
    private TextField keywordTextField;
    @FXML
    private DatePicker datePicker;
    private FlickrService flickrService;
    private GoogleMapView mapView;
    private GoogleMap googleMap;
    private Marker actualMarker;
    private LatLong actualLatLong;
    private ArrayList<ImageTile> selectedImages = new ArrayList<ImageTile>();
    private Cancelable downloadTask;

    public FlickrDialog(Node origin, String title, FlickrService flickrService) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, FlickrDialog.class, "view/FlickrDialog.fxml");
        GoogleMapsScene mapsScene = new GoogleMapsScene();
        this.mapView = mapsScene.getMapView();
        this.mapContainer.getChildren().add(mapsScene.getMapView());
        this.flickrService = flickrService;
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
        mapView.addMapInializedListener(new MapComponentInitializedListener() {
            @Override
            public void mapInitialized() {
                //wait for the map to initialize.
                googleMap = mapView.getMap();
                googleMap.addUIEventHandler(UIEventType.dblclick, (JSObject obj) -> {
                    //googleMap.setZoom(googleMap.getZoom()-1); //workaround to prevent zoom on doubleclick
                    dropMarker(new LatLong((JSObject) obj.getMember("latLng")));
                });
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
        progressBar.getStyleClass().add("progress-bar");
        progressBar.setTooltip(new Tooltip("Fotos werden heruntergeladen..."));
        stopButton.setTooltip(new Tooltip("Abbrechen"));
        datePicker.setValue(LocalDate.now());
        datePicker.setTooltip(new Tooltip("Wählen Sie ein Datum für die Fotos aus"));
    }


    public void addKeyword(String keyword) {
        HBox hbox = new HBox();
        hbox.setStyle("-fx-background-radius: 5; -fx-background-color: #E91E63; ");
        Text text = new Text(keyword);
        text.setFill(Color.WHITE);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(3, 5, 5, 5));
        hbox.getChildren().add(text);
        Text x = new Text("x");
        x.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                keywordsFlowPane.setCursor(Cursor.HAND);
            }
        });
        x.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                keywordsFlowPane.setCursor(Cursor.DEFAULT);
            }
        });
        x.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                keywordsFlowPane.getChildren().remove(hbox);
                flickrService.reset();
                downloadButton.setText("Fotos herunterladen");
            }
        });
        hbox.getChildren().add(new Text("  "));
        hbox.getChildren().add(x);
        keywordsFlowPane.getChildren().add(hbox);
        flickrService.reset();
        downloadButton.setText("Fotos herunterladen");
    }

    private void dropMarker(LatLong ll) {
        if (actualMarker != null) {
            googleMap.removeMarker(actualMarker);
        }
        this.actualLatLong = ll;
        googleMap.addMarker(actualMarker = new Marker(new MarkerOptions().position(ll)));
        flickrService.reset();
        downloadButton.setText("Fotos herunterladen");
    }

    @FXML
    public void handleOnDownloadButtonClicked() {
        downloadButton.setDisable(true);
        progress.setVisible(true);
        downloadPhotos();
    }

    @FXML
    public void handleOnImportButtonClicked() {
        ArrayList<Photo> photos = new ArrayList<Photo>();
        for (ImageTile i : selectedImages) {
            Photo p = i.getPhoto();
            p.setDatetime(datePicker.getValue().atStartOfDay());
            photos.add(p);
            logger.debug("Added photo for import {}", p);
        }
        setResult(photos);
        selectedImages.clear();
        close();
    }

    @FXML
    public void handleOnStopButtonClicked() {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(borderPane, "Download abbrechen", "Download abbrechen?");
        Optional<Boolean> confirmed = confirmationDialog.showForResult();
        if (!confirmed.isPresent() || !confirmed.get()) return;
        logger.debug("Canceling the download...");
        if (downloadTask != null)
            downloadTask.cancel();
        progress.setVisible(false);
        progressBar.setProgress(0.0);
        downloadButton.setDisable(false);

    }

    private void downloadPhotos() {
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

            if (actualMarker != null) {
                latitude = actualLatLong.getLatitude();
                longitude = actualLatLong.getLongitude();
                useGeoData = true;
            }
            downloadTask = flickrService.downloadPhotos(tags, latitude, longitude, useGeoData, new Consumer<Photo>() {

                        public void accept(Photo photo) {

                            Platform.runLater(new Runnable() {

                                public void run() {
                                    ImageTile imageTile = new ImageTile(photo);
                                    imageTile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {
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

                    },
                    new Consumer<Double>() {

                        public void accept(Double downloadProgress) {

                            Platform.runLater(new Runnable() {

                                public void run() {
                                    progressBar.setProgress(downloadProgress);
                                    if (downloadProgress == 1.0) {
                                        progress.setVisible(false);
                                        progressBar.setProgress(0.0);
                                        downloadButton.setDisable(false);
                                        downloadButton.setText("Mehr herunterladen");
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

    /**
     * Widget for one widget in the image grid. Can either be in a selected or an unselected state.
     */
    private class ImageTile extends HBox {

        private BooleanProperty selected = new SimpleBooleanProperty(false);

        private Photo photo;

        private Image image;
        private ImageView imageView;

        public ImageTile(Photo photo) {

            this.photo = photo;

            try {
                image = new Image(new FileInputStream(new File(photo.getPath())), 150, 0, true, true);
            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }

            imageView = new ImageView(image);
            imageView.setFitWidth(150);

            getStyleClass().add("image-tile-non-selected");


            this.getChildren().add(imageView);
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

        public Photo getPhoto() {
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
}
