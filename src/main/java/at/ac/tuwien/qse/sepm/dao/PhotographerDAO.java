package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface PhotographerDAO {

    /**
     * Store a new photographer.
     *
     * @param p Photographer to create
     * @return The created photographer
     * @throws ValidationException If the photographer is invalid
     * @throws DAOException If the photographer cannot be created or the data store fails to create a record.
     */
    Photographer create(Photographer photographer) throws DAOException, ValidationException;

    /**
     * Update given photgrapher
     *
     * @param photographer The photographer to be updated.
     * @throws DAOException If the photographer can not be updated.
     * @throws ValidationException If the photographer is invalid
     */
    void update(Photographer photographer) throws DAOException, ValidationException;

    /**
     * Reads a single photographer by the id
     * @param p the photographer object containing the id
     * @return the read photographer
     * @throws DAOException If the data store fails to retrieve the record or if the photographer doesn't exist.
     */
    Photographer read(Photographer photographer) throws DAOException;

    /**
     * Retrieve all existing photographers.
     *
     * @return A List of all currently known photographers.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    List<Photographer> readAll() throws DAOException;
}
