package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import com.lynden.gmapsfx.GoogleMapView;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import org.controlsfx.tools.Platform;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller for the inspector view which is used for modifying meta-data of a photo.
 */
public class Inspector {

    @FXML private BorderPane root;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    @FXML
    private TableColumn<Pair<String, Object>, Object> exifValue;

    @FXML
    private TableColumn<Pair<String, Object>, Object> exifName;

    @FXML
    private TableView<Pair<String, Object>> exifTable;



//   private Photo photo = null;

    @FXML private VBox contentBox2;

   // @FXML private Label proofOfConceptLabel;

   private GoogleMapsScene mapsScene;

    @FXML private Label proofOfConceptLabel;

    @Autowired private Organizer organizer;

    private Photo photo = null;
    @Autowired private PhotoService photoservice;


    public Inspector() {

    }

    /**
     * Set the active photo.
     *
     * The photos metadate will be displayed in the inspector widget.
     *
     * @param photo The active photo for which to show further information
     */
    public void setActivePhoto(Photo photo) {
        this.photo = photo;

        proofOfConceptLabel.setText("Selected photo is: " + photo.getPath());

        Exif exif = photo.getExif();
        ObservableList<Pair<String, Object>> exifData = FXCollections.observableArrayList(
                new Pair<String, Object>("Aufnahmedatum", exif.getDate().toString()),
                new Pair<String, Object>("Kamerahersteller", exif.getMake()),
                new Pair<String, Object>("Kameramodell", exif.getModel())
        );

        exifTable.setEditable(true);
        exifName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<String, Object>, Object>, ObservableValue<Object>>() {
            @Override
            public ObservableValue<Object> call(TableColumn.CellDataFeatures<Pair<String, Object>, Object> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getKey());
            }
        });

        exifValue.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<String, Object>, Object>, ObservableValue<Object>>() {
            @Override
            public ObservableValue<Object> call(TableColumn.CellDataFeatures<Pair<String, Object>, Object> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getKey());
            }
        });

        exifValue.setCellFactory((param) -> new ExifValueCell());
        exifTable.setItems(exifData);

        //this.mapsScene = new GoogleMapsScene(photo.getExif());
        //contentBox2.getChildren().clear();

        //contentBox2.getChildren().add(mapsScene.getMapView());

        mapsScene.addMarker(photo);


    }

    @FXML
    private void initialize() {
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
        this.mapsScene= new GoogleMapsScene();
        contentBox2.getChildren().add(mapsScene.getMapView());



    }

    private void handleDelete(Event event) {
        if(photo!=null){

            List<Photo> photolist = new ArrayList<Photo>();
            photolist.add(photo);
           organizer.reloadPhotos();
            try {
                photoservice.deletePhotos(photolist);
            } catch (ServiceException e) {
                System.out.println(e);
            }
        }
    }

    private void handleCancel(Event event) {
        // TODO
    }

    private void handleConfirm(Event event) {
        // TODO
    }

    class ExifValueCell extends TableCell<Pair<String, Object>, Object> {
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                if (item instanceof String) {
                    TextField f = new TextField((String) item); // z.b. ein textfield zum editieren. Hier kann auch ein signalhandler auf aenderungen horchen
                    setGraphic(f);
                } else if (item instanceof Integer) {
                    setText(Integer.toString((Integer) item));
                    setGraphic(null);
                } else if (item instanceof Boolean) {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected((boolean) item);
                    setGraphic(checkBox);
                } else {
                    setText("N/A");
                    setGraphic(null);
                }
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
