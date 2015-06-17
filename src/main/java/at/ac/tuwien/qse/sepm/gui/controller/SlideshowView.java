package at.ac.tuwien.qse.sepm.gui.controller;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import javafx.collections.ObservableList;

import java.util.List;

public interface SlideshowView {
    ObservableList<Slideshow> getSlideshows();
    void addPhotosToSlideshow(List<Photo> photos, Slideshow slideshow);
}
