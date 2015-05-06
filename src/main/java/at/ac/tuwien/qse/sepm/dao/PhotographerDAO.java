package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photographer;

import java.util.List;

public interface PhotographerDAO {

    /**
     * Store a new photographer.
     *
     * @param p Photographer to create
     * @return The created photographer
     * @throws DAOException If the photographer cannot be copied or the data store fails to create a record.
     */
    public Photographer create(Photographer p) throws DAOException;

    public Photographer read(Photographer p) throws DAOException;

    public List<Photographer> readAll() throws DAOException;
}
