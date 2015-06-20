package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.TitleSlide;

public class TitleSlideTile extends SlideTileBase<TitleSlide> {

    public TitleSlideTile(TitleSlide slide) {
        super(slide);
        getStyleClass().add("title");
    }
}
