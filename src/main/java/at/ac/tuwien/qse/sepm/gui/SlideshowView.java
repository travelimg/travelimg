package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.gui.controller.impl.WorldmapViewImpl;
import org.apache.logging.log4j.LogManager;

public class SlideshowView {

    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(WorldmapViewImpl.class);
    private GoogleMapsScene map;

    public GoogleMapsScene getMap() {
        return this.map;
    }

    public void setMap(GoogleMapsScene map) {
        logger.debug("Worldmap wird erstellt");

        this.map = map;


    }
}
