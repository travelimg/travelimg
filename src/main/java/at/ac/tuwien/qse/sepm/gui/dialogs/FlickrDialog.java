package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.IOHandler;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    private ProgressIndicator progressIndicator;
    @FXML
    private FlowPane photosFlowPane;
    @FXML
    private Button searchButton, importButton, cancelButton, fullscreenButton;
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

    private FlickrService flickrService;
    private ExifService exifService;
    private IOHandler ioHandler;
    private LatLong actualLatLong;
    private ArrayList<ImageTile> selectedImages = new ArrayList<ImageTile>();
    private Cancelable downloadTask;

    public FlickrDialog(Node origin, String title, FlickrService flickrService, ExifService exifService, IOHandler ioHandler) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, FlickrDialog.class, "view/FlickrDialog.fxml");

        this.flickrService = flickrService;
        this.exifService = exifService;
        this.ioHandler = ioHandler;
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

        mapScene.setDoubleClickCallback((position) -> dropMarker(position));

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

        cancelButton.setOnAction(e -> close());
        importButton.setOnAction(e -> handleImport());
        searchButton.setOnAction(e -> handleSearch());
        fullscreenButton.setOnAction(e -> handleFullscreen());
    }

    private void handleFullscreen() {

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
            }
        });
        hbox.getChildren().add(new Text("  "));
        hbox.getChildren().add(x);
        keywordsFlowPane.getChildren().add(hbox);
        flickrService.reset();
    }

    private void dropMarker(LatLong ll) {
        if (actualLatLong != null) {
            mapScene.clear();
        }
        this.actualLatLong = ll;
        mapScene.addMarker(ll);
        flickrService.reset();
        searchButton.setText("Fotos herunterladen");
    }

    public void handleSearch() {
        searchButton.setDisable(true);
        progressIndicator.setVisible(true);
        downloadPhotos();
    }

    public void handleImport() {
        ArrayList<Photo> photos = new ArrayList<Photo>();
        for (ImageTile i : selectedImages) {
            Photo p = i.getPhoto();
            p.getData().setDatetime(datePicker.getValue().atStartOfDay());
            //exifService.setDateAndGeoData(p);
            photos.add(p);
            //Path path = Paths.get(p.getPath());
            //ioHandler.copyFromTo(Paths.get(p.getPath()),Paths.get(System.getProperty("user.home"),"travelimg/"+path.getFileName()));
            logger.debug("Added photo for import {}", p);
        }
        selectedImages.clear();
        setResult(photos);
        close();
    }

    public void handleStop() {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(borderPane, "Download abbrechen", "Download abbrechen?");
        Optional<Boolean> confirmed = confirmationDialog.showForResult();
        if (!confirmed.isPresent() || !confirmed.get()) return;
        logger.debug("Canceling the download...");
        if (downloadTask != null)
            downloadTask.cancel();
        searchButton.setDisable(false);

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

            if (actualLatLong != null) {
                latitude = actualLatLong.getLatitude();
                longitude = actualLatLong.getLongitude();
                useGeoData = true;
            }
            downloadTask = flickrService.searchPhotos(tags, latitude, longitude, useGeoData,
                    new Consumer<Photo>() {

                        public void accept(Photo photo) {

                            Platform.runLater(new Runnable() {

                                public void run() {
                                    ImageTile imageTile = new ImageTile(photo);
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
