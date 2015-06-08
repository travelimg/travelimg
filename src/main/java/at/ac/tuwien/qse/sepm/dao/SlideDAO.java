package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

/**
 * Created by mb on 08.06.15.
 */
public interface SlideDAO {

    /**
     * Insert the photo wich is addedTo the specified Slideshow
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */

    void create(Slide slide) throws DAOException, ValidationException;

    /**
     * Delete the photo from the slide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */

    void delete(Slide slide) throws DAOException, ValidationException;

    /**
     * Retrieve a list of slides from a given slideshow
     * @return
     * @throws DAOException
     */

    List<Slide> readAll() throws DAOException;


}
