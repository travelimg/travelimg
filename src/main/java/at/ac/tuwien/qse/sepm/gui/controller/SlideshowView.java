package at.ac.tuwien.qse.sepm.gui.controller;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import javafx.collections.ObservableList;

import java.util.List;

public interface SlideshowView {

    /**
     * Return all slideshows.
     *
     * @return An observable list of all slideshows which gets updated when slideshows change, are added or deleted.
     */
    ObservableList<Slideshow> getSlideshows();

    /**
     * Add the given list of photos to the specified slideshow.
     *
     * @param photos The list of photos which should be part of the slideshow.
     * @param slideshow The slideshow to which the photos should be added.
     */
    void addPhotosToSlideshow(List<Photo> photos, Slideshow slideshow);
}
