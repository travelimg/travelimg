package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;

import java.util.List;

public interface ClusterService {

    void cluster(List<Photo> photos) throws ServiceException;

    /**
     * Read and return all currently saved journeys.
     *
     * @return list of all currently saved journeys
     * @throws ServiceException propagates DAOExceptions
     */
    List<Journey> getAllJourneys() throws ServiceException;

    Journey addJourney(Journey journey) throws ServiceException;


    /**
     *
     * @param journey
     * @return
     * @throws ServiceException
     */
    List<Place> clusterJourney(Journey journey) throws ServiceException;

//    /**
//     *
//     * @param journey
//     * @return
//     * @throws ServiceException
//     */
////    Journey addJourney(Journey journey) throws ServiceException;
////
////    Place addPlace(Place place) throws ServiceException;
}
