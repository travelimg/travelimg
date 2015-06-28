package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface SlideshowDAO {

    /**
     *
     * @param slideshow to create
     * @return The created slideshow
     * @throws DAOException if the slideshow cannot be created or the data store fails to create a record
     * @throws ValidationException if the diashow is invalid
     */
    Slideshow create(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Update given slideshow
     * @param slideshow to be updated
     * @return The newly updated slideshow.
     * @throws DAOException if the slideshow can not be updated
     * @throws ValidationException if the slideshow is invalid
     */
    Slideshow update(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Delete a selected slideshow
     * @param slideshow of the desired slideshow
     * @return the deleted slideshow
     * @throws DAOException If the data store fails to retrieve the record or if the slideshow doesnt exists
     */
    void delete(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Retrieve all existing slideshows
     * @return a List of all existing slideshows
     * @throws DAOException If the data store fails to retrieve the record or if the slideshow doesnt exists
     */
    List<Slideshow> readAll() throws DAOException;
}
