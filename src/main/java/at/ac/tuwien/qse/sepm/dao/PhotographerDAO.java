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
     * @throws DAOException If the photographer cannot be copied or the data store fails to create a record.
     */
    public Photographer create(Photographer p) throws DAOException, ValidationException;

    /**
     * Reads a single photographer by the id
     * @param p the photographer object containing the id
     * @return the read photographer
     * @throws DAOException If the data store fails to retrieve the record or if the photographer doesn't exist.
     */
    public Photographer read(Photographer p) throws DAOException;

    /**
     * Retrieve all existing photographers.
     *
     * @return A List of all currently known photographers.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    public List<Photographer> readAll() throws DAOException;
}
