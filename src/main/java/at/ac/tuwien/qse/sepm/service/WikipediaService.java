package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;

public interface WikipediaService {

    /**
     * Create and return a WikiPlaceInfo object for the given Place <tt>place</tt>.
     *
     * @param place must contain non-null and non-empty entries for <tt>place.city</tt>
     *              <tt>place.country</tt>
     * @return a new WikiPlaceInfo object for the given Place. The fields may not all be
     *     initialized - sometimes not all information can be found
     * @throws ServiceException if an exception occurs on service layer level
     */
    WikiPlaceInfo getWikiPlaceInfo(Place place) throws ServiceException;
}
