package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.service.WikipediaService;

public class WikipediaServiceImpl implements WikipediaService {

    /**
     * Create and return a WikiPlaceInfo object for the given Place <tt>place</tt>.
     *
     * @param place must contain non-null and non-empty entries for <tt>place.city</tt>
     *              <tt>place.country</tt>
     * @return a new WikiPlaceInfo object for the given Place. The fields may not all be
     *     initialized - sometimes not all information can be found
     * @throws DAOException if an exception occurs on DAO level
     */
    @Override
    public WikiPlaceInfo createWikiPlaceInfo(Place place) throws DAOException {
        //TODO implement method
        return null;
    }
}
