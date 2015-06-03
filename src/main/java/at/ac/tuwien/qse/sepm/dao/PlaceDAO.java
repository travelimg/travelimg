package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;


public interface PlaceDAO {
    Place create(Place place) throws DAOException, ValidationException;

    void delete(Place place) throws DAOException, ValidationException;

    void update(Place place) throws DAOException, ValidationException;

    List<Place> readAll() throws DAOException;

    Place getById(int id) throws DAOException;
}
