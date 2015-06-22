package at.ac.tuwien.qse.sepm.gui.controller.impl;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.GoogleMapsScene;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class WorldmapViewImpl implements WorldmapView {
    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(WorldmapViewImpl.class);

    @FXML
    private BorderPane border;
    private GoogleMapsScene worldMap;
    @Autowired
    private PhotoService photoService;

    @Override
    public GoogleMapsScene getMap() {
        return null;//this.worldMap;
    }

    @Override
    public void setMap(GoogleMapsScene map) {
        /*this.worldMap = map;

        worldMap.removeAktiveMarker();
        try {
            worldMap.addMarkerList(deleteDouble(photoService.getAllPhotos()));

        } catch (ServiceException e) {
            logger.debug(e);
        }
        worldMap.setCenter(70.7385, -90.9871);
        worldMap.setZoom(2);
        border.setCenter(worldMap.getMapView());*/
    }

    /**
     * checks whether a marker representing a photo and is already available
     *
     * @param list List of photos which should be displayed as a marker
     * @param p    a photo to be added
     * @return true if the Photo is entitled to be added
     */
    private boolean checkDouble(List<Photo> list, Photo p) {

        for (Photo photo : list) {
            if (p.getData().getLatitude() == photo.getData().getLatitude() && p.getData().getLongitude() == photo.getData().getLongitude()) {
                logger.debug("Marker already exists");
                return true;

            }

            if (Math.abs(p.getData().getLatitude() - photo.getData().getLatitude()) < 1 && Math.abs(p.getData().getLongitude() - photo.getData().getLongitude()) < 1) {
                logger.debug("Marker with similar coordinates already exists");
                return true;
            }

        }
        logger.debug("set Marker ");
        return false;
    }

    /**
     * delete Photos where Long and Lat is similar to display a new Marker
     *
     * @param l List of Photos to be set as Marker
     * @return List of photos which should be displayed as a marker
     */
    private List<Photo> deleteDouble(List<Photo> l) {
        List<Photo> list = new ArrayList<Photo>();
        for (Photo p : l) {
            if (list.size() == 0) {
                list.add(p);
            } else if (!checkDouble(list, p)) {
                list.add(p);
            }
        }
        return list;
    }


}
