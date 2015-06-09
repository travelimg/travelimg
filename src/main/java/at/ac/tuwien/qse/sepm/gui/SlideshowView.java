package at.ac.tuwien.qse.sepm.gui;

import org.apache.logging.log4j.LogManager;

public class SlideshowView {

    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(WorldmapView.class);
    private GoogleMapsScene map;

    public GoogleMapsScene getMap() {
        return this.map;
    }

    public void setMap(GoogleMapsScene map) {
        logger.debug("Worldmap wird erstellt");

        this.map = map;


    }
}
