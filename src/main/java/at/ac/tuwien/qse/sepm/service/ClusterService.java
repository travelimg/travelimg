package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;

import java.util.List;

/**
 * Created by David on 15.05.2015.
 */
public interface ClusterService {
    void cluster(List<Photo> photos) throws ServiceException;
}
