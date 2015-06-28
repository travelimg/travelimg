package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Slide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlideTileBase<S extends Slide> extends SlideTile {

    private static final Logger LOGGER = LogManager.getLogger();

    private final S slide;

    public SlideTileBase(S slide) {
        super(slide);
        this.slide = slide;
    }
}
