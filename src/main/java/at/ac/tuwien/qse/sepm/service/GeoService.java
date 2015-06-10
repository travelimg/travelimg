package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Place;

public interface GeoService {

    /**
     * Does a reverse geocoding, i.e returns the name of a place for given coordinates.
     * @param latitude
     * @param longitude
     * @return The place at the requested position, containing its city and country information.
     * @throws ServiceException if the coordinates are invalid.
     */
    Place getPlaceByGeoData(double latitude, double longitude) throws ServiceException;
}
