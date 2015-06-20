package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.*;
import javafx.scene.layout.StackPane;

public class SlideView extends StackPane {

    public static SlideView of(Slide slide) {
        if (slide instanceof PhotoSlide) {
            return new PhotoSlideView((PhotoSlide)slide, null);
        } else if (slide instanceof MapSlide) {
            return new MapSlideView((MapSlide)slide);
        } else if (slide instanceof TitleSlide) {
            return new TitleSlideView((TitleSlide)slide);
        }

        return null;
    }
}
