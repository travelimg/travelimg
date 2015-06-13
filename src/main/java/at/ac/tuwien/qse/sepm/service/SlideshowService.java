package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public interface SlideshowService {

    /**
     * Create a Slideshow in the data store.
     *
     * @param slideshow  which to create; must not be null; must not already have an id
     * @return the created slideshow
     * @throws ServiceException If the Slideshow can not be created or the data store fails to
     *      create a record.
     */
    void create(Slideshow slideshow) throws ServiceException;

    /**
     * Delete an existing Slideshow.
     *
     * @param slideshow Specifies which slideshow to delete by providing the id;
     *            must not be null;
     *            <tt>slideshow.id</tt> must not be null;
     * @throws ServiceException If the Slideshwo can not be deleted or the data store fails to
     *     delete the record.
     */
    void delete(Slideshow slideshow) throws ServiceException;

    /**
     * Return a list of all existing slideshows.
     *
     * @return the list of all available slideshows
     * @throws ServiceException if retrieval failed
     */
    List<Slideshow> getAllSlideshows() throws ServiceException;

    List<Slide> addPhotosToSlideshow(List<Photo> photos, Slideshow slideshow) throws ServiceException;
}
