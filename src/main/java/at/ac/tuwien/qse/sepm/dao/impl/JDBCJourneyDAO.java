package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.JourneyDAO;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

/**
 * Created by David on 21.05.2015.
 */
public class JDBCJourneyDAO extends JDBCDAOBase implements JourneyDAO {
    @Override public Journey create(Journey journey) throws DAOException, ValidationException {
        return null;
    }

    @Override public void delete(Journey journey) throws DAOException, ValidationException {

    }

    @Override public void update(Journey journey) throws DAOException, ValidationException {

    }

    @Override public List<Journey> readAll() throws DAOException {
        return null;
    }

    @Override public Journey getByName(String name) throws DAOException {
        return null;
    }
}
