package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public interface SlideDAO<S extends Slide> {

    /**
     * Insert the photo wich is addedTo the specified Slideshow
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    S create(S slide) throws DAOException, ValidationException;

    /**
     * Delete the photo from the slide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    void delete(S slide) throws DAOException, ValidationException;

    S update(S slide) throws DAOException, ValidationException;

    List<S> getSlidesForSlideshow(int slideshowId) throws DAOException, ValidationException;
}
