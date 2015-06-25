package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface JourneyDAO {
    /**
     * Create the journey in the data store. If the journey already exists the existing entry is returned.
     *
     * @param journey Journey which to create.
     * @return The created Journey
     * @throws DAOException        If the data store fails to create a record.
     * @throws ValidationException If the journey is not a valid entity.
     */
    Journey create(Journey journey) throws DAOException, ValidationException;

    /**
     * Delete an existing journey.
     *
     * @param journey Specifies which journey to delete by providing the id.
     * @throws DAOException        If the data store fails to delete the record.
     * @throws ValidationException If the journey is not a valid entity.
     */
    void delete(Journey journey) throws DAOException, ValidationException;

    /**
     * Update an existing journey.
     *
     * @param journey Description of the journey to update together with the new values.
     * @throws DAOException        If the data store fails to update the record.
     * @throws ValidationException If the journey is not a valid entity.
     */
    void update(Journey journey) throws DAOException, ValidationException;

    /**
     * Retrieve a list of all journeys.
     *
     * @return List with all journeys in the data store.
     * @throws DAOException If the data store fails to deliver all journey records.
     */
    List<Journey> readAll() throws DAOException;

    /**
     * Retrive a journey by its id.
     *
     * @param id The name of the data store entry.
     * @return Returns journey record with the given id.
     * @throws DAOException        If the data store fails to deliver the record.
     * @throws ValidationException If the id is invalid.
     */
    Journey getByID(int id) throws DAOException, ValidationException;

    /**
     * Retrive a journey by its name.
     *
     * @param name The name of the data store entry.
     * @return Returns journey record with the given name.
     * @throws DAOException        If the data store fails to deliver the record.
     */
    Journey getByName(String name) throws DAOException;
}
