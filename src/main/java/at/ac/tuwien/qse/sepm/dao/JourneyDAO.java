package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

/**
 * Created by David on 21.05.2015.
 */
public interface JourneyDAO {
    // TODO: handle ID in JourneyDAO
    Journey create(Journey journey) throws DAOException, ValidationException;

    void delete(Journey journey) throws DAOException, ValidationException;

    void update(Journey journey) throws DAOException, ValidationException;

    List<Journey> readAll() throws DAOException;

    Journey getByName(String name) throws DAOException;
}
