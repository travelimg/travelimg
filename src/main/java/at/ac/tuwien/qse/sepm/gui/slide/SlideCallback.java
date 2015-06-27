package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;

public interface SlideCallback<D> {

    default void handle(PhotoSlide slide) {}
    default void handle(MapSlide slide) {}
    default void handle(TitleSlide slide) {}

    default void handle(PhotoSlide slide, D data) {}
    default void handle(MapSlide slide, D data) {}
    default void handle(TitleSlide slide, D data) {}
}
