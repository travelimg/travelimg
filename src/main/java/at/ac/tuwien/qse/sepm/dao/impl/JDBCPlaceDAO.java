package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PlaceDAO;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

/**
 * Created by David on 21.05.2015.
 */
public class JDBCPlaceDAO extends JDBCDAOBase implements PlaceDAO {
    @Override public Place create(Place place) throws DAOException, ValidationException {
        return null;
    }

    @Override public void delete(Place place) throws DAOException, ValidationException {

    }

    @Override public void update(Place place) throws DAOException, ValidationException {

    }

    @Override public List<Place> readAll() throws DAOException {
        return null;
    }

    @Override public Place getByCityName(String name) throws DAOException {
        return null;
    }
}
