package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;

public interface PhotoDAO {

    public Photo create(Photo p) throws DAOException;
    public Photo read(Photo p) throws DAOException;
    public void update(Photo p) throws DAOException;
    public void delete(Photo p) throws DAOException;
    public Photo readAll() throws DAOException;

}
