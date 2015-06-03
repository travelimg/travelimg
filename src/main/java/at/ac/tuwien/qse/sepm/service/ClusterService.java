package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;

import java.util.List;

public interface ClusterService {

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
