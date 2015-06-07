package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;

public class SlideshowView {

    private GoogleMapsScene map;
    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(WorldmapView.class);


    public void setMap(GoogleMapsScene map){
        logger.debug("Worldmap wird erstellt");

        this.map = map;


    }
    public GoogleMapsScene getMap(){
        return this.map;
    }
}
