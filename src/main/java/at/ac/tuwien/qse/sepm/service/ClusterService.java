package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
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
     * Creates a journey and clusters this journey in places.
     *
     * A journey entry is made in the data record.
     * This journey is then clustered into places (places are at least 1 degree apart from each other)
     * @param journey Journey to be clustered.
     * @return Returns a list with all the clusters(places).
     * @throws ServiceException If cluster-service fails due to database errors.
     */
    List<Place> clusterJourney(Journey journey) throws ServiceException;
}
