package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.GoogleMapsScene;import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by christoph on 21.05.15.
 */
public class MainNavigation  {
    private static final Logger logger = LogManager.getLogger();

    @FXML private TabPane root;
    @FXML private Tab gitter;
    @FXML private Tab weltkarte;
    @FXML private Tab timeline;
    @FXML private ScrollPane scrollPane;
    @FXML private Insets in;
    @FXML private TilePane tilePane;
    private GoogleMapsScene worldMap;
    @Autowired private PhotoService photoService;


    public MainNavigation(){
        scrollPane = new ScrollPane();
        in = new Insets(15,15,15,15);
        tilePane = new TilePane();
        scrollPane.setPrefWidth(500);
        scrollPane.setPrefHeight(400);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        tilePane.setHgap(15);
        tilePane.setVgap(15);
        tilePane.setPadding(in);
        scrollPane.setContent(tilePane);

    }

    @FXML
    private void initialize() {

        worldMap = new GoogleMapsScene(getAllPhotos());
       weltkarte.setContent(worldMap.getMapView());
        gitter.setContent(scrollPane);



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
}
