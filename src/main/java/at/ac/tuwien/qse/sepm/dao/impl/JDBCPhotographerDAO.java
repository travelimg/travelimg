package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.entities.Photographer;

import java.util.List;

public class JDBCPhotographerDAO extends JDBCDAOBase implements PhotographerDAO {
    public Photographer create() throws DAOException {
        return null;
    }

    public void update(Photographer p) throws DAOException {

    }

    public Photographer read(Photographer p) throws DAOException {
        return null;
    }

    public List<Photographer> readAll() throws DAOException {
        return null;
    }
}
