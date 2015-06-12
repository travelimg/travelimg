package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;


public interface PlaceDAO {
    /**
     * Create the place in the data store. If the place already exists the existing entry is returned.
     *
     * @param place Place which to create.
     * @return The created Place
     * @throws DAOException        If the data store fails to create a record.
     * @throws ValidationException If the place is not a valid entity.
     */
    Place create(Place place) throws DAOException, ValidationException;

    /**
     * Update an existing place.
     *
     * @param place Description of the place to update together with the new values.
     * @throws DAOException        If the data store fails to update the record.
     * @throws ValidationException If the place is not a valid entity.
     */
    void update(Place place) throws DAOException, ValidationException;

    /**
     * Retrieve a list of all places.
     *
     * @return List with all places in the data store.
     * @throws DAOException If the data store fails to deliver all place records.
     */
    List<Place> readAll() throws DAOException;

    /**
     * Retrive a place by its id
     *
     * @param id The id of the data store entry.
     * @return Returns place record with the given id.
     * @throws DAOException        If the data store fails to deliver the record.
     * @throws ValidationException If the id is invalid.
     */
    Place getById(int id) throws DAOException, ValidationException;

    /**
     * Retrieve places of a journey
     *
     * @param journey The journey containing the id
     * @return Returns the places
     * @throws DAOException        If the data store fails to deliver the records.
     * @throws ValidationException If the id is invalid.
     */
    List<Place> readByJourney(Journey journey) throws DAOException, ValidationException;

    /**
     * Create and return a WikiPlaceInfo object for the given Place <tt>place</tt>.
     *
     * @param place must contain non-null and non-empty entries for <tt>place.city</tt>
     *              <tt>place.country</tt>
     * @return a new WikiPlaceInfo object for the given Place. The fields may not all be
     *     initialized - sometimes not all information can be found
     * @throws DAOException if an exception occurs on DAO level
     * @throws ValidationException if <tt>place</tt> isn't a valid object
     */
    WikiPlaceInfo createWikiPlaceInfo(Place place) throws DAOException, ValidationException;
}
