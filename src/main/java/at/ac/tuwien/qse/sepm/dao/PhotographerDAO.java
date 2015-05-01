package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photographer;

import java.util.List;

public interface PhotographerDAO {

    public Photographer create() throws DAOException;
    public void update(Photographer p) throws DAOException;
    public Photographer read(Photographer p) throws DAOException;
    public List<Photographer> readAll() throws DAOException;
}
