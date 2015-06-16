package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;

import java.util.List;

public interface ClusterService {

    /**
     * Read and return all currently saved journeys.
     *
     * @return list of all currently saved journeys
     * @throws ServiceException propagates DAOExceptions
     */
    List<Journey> getAllJourneys() throws ServiceException;

    /**
     * Read and return all currently saved places.
     *
     * @return list of all currently saved places
     * @throws ServiceException propagates DAOExceptions
     */
    List<Place> getAllPlaces() throws ServiceException;

    /**
     * Read and return places of a journey.
     *
     * @param journey the journey containing its id.
     * @return the places
     * @throws ServiceException propagates DAOExceptions
     */
    List<Place> getPlacesByJourney(Journey journey) throws ServiceException;

    /**
     * Adds a new place to the datastore.
     *
     * @param place Place to be created in the datastore.
     * @return the newly created place
     * @throws ServiceException If creation of record fails.
     */
    Place addPlace(Place place) throws ServiceException;

    /**
     * Adds a new journey to the datastore.
     *
     * @param journey Journey to be created in the datastore.
     * @return the newly created journey
     * @throws ServiceException If creation of record fails.
     */
    Journey addJourney(Journey journey) throws ServiceException;

    /**
     * Creates a journey and clusters this journey in places.
     * <p>
     * A journey entry is made in the data record.
     * This journey is then clustered into places (places are at least 1 degree apart from each other)
     *
     * @param journey Journey to be clustered.
     * @return Returns a list with all the clusters(places).
     * @throws ServiceException If cluster-service fails due to database errors.
     */
    List<Place> clusterJourney(Journey journey) throws ServiceException;
}
