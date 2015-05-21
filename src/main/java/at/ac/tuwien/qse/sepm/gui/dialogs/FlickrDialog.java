package at.ac.tuwien.qse.sepm.gui.dialogs;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.GoogleMapsScene;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.FlickrServiceImpl;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class FlickrDialog extends Dialog {

    @FXML private HBox progress;
    @FXML private ProgressBar progressBar;
    @FXML private FlowPane photosFlowPane;
    @FXML private Button downloadButton;
    @FXML private Pane mapContainer;
    @FXML private FlowPane keywordsFlowPane;
    @FXML private TextField keywordTextField;
    @Autowired private FlickrService flickrService;

    private GoogleMapView mapView;
    private GoogleMap googleMap;
    private Marker actualMarker;
    private LatLong actualLatLong;

    public FlickrDialog(Node origin, String title) {
        super(origin, title);
        FXMLLoadHelper.load(this, this, FlickrDialog.class, "view/FlickrDialog.fxml");
        GoogleMapsScene mapsScene = new GoogleMapsScene();
        this.mapView = mapsScene.getMapView();
        this.mapContainer.getChildren().add(mapsScene.getMapView());
        this.flickrService = new FlickrServiceImpl(); //TODO should be removed as we are using spring
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
    }

    public void addKeyword(String keyword){
        HBox hbox = new HBox();
        hbox.setStyle("-fx-background-radius: 5; -fx-background-color: #8BC34A; ");
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
            }
        });
        hbox.getChildren().add(new Text("  "));
        hbox.getChildren().add(x);
        keywordsFlowPane.getChildren().add(hbox);
    }

    private void dropMarker(LatLong ll){
        if(actualMarker!=null){
            googleMap.removeMarker(actualMarker);
        }
        this.actualLatLong = ll;
        googleMap.addMarker(actualMarker = new Marker(new MarkerOptions().position(ll)));
    }

    @FXML
    public void handleOnDownloadButtonClicked(){
        downloadButton.setDisable(true);
        progress.setVisible(true);
        downloadPhotos();
    }

    private void downloadPhotos(){
        ObservableList<Node> test = keywordsFlowPane.getChildren();
        String tags[] = new String[test.size()];
        for(int i = 0; i<test.size(); i++){
            HBox h = (HBox) test.get(i);
            tags[i] = ((Text)h.getChildren().get(0)).getText();
        }

        try {
            double latitude = 0.0;
            double longitude = 0.0;
            boolean useGeoData = false;

            if(actualMarker!=null){
                latitude = actualLatLong.getLatitude();
                longitude = actualLatLong.getLongitude();
                useGeoData = true;
            }
            flickrService.downloadPhotos(tags,latitude, longitude,true,useGeoData,new Consumer<Photo>() {

                public void accept(Photo photo) {

                    Platform.runLater(new Runnable() {

                        public void run() {
                            ImageView imageView = null;
                            try {
                                final Image image;
                                image = new Image(new FileInputStream(new File(photo.getPath())), 150, 0, true, true);
                                imageView = new ImageView(image);
                                imageView.setFitWidth(150);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            photosFlowPane.getChildren().add(imageView);
                        }
                    });

                }

            },
                    new Consumer<Double>() {

                        public void accept(Double downloadProgress) {

                            Platform.runLater(new Runnable() {

                                public void run() {
                                    progressBar.setProgress(downloadProgress);
                                    if(downloadProgress==1.0){
                                        progress.setVisible(false);
                                        progressBar.setProgress(0.0);
                                        downloadButton.setDisable(false);
                                        downloadButton.setText("Mehr herunterladen");
                                    }
                                }
                            });

                        }

                    }


                    ,new ErrorHandler<ServiceException>() {

                public void handle(ServiceException exception) {
                    //handle errors here
                }
            });
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
