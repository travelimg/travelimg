package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public interface SlideshowDAO {

    /**
     *
     * @param slideshow to create
     * @return The created slideshow
     * @throws DAOException if the slideshow cannot be created or the data store fails to create a record
     * @throws ValidationException if the diashow is invalid
     */
    void create(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Update given slideshow
     * @param slideshow to be updated
     * @throws DAOException if the slideshow can not be updated
     * @throws ValidationException if the slideshow is invalid
     */
    void update(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Reads a single slideshow by the id
     * @param id of the desired slideshow
     * @return the read slideshow
     * @throws DAOException If the data store fails to retrieve the record or if the slideshow doesnt exists
     */

    Slideshow getById(int id) throws  DAOException;

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
