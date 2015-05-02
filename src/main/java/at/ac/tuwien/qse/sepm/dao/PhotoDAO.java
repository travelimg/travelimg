package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.util.List;

public interface PhotoDAO {

    public Photo create(Photo p) throws DAOException;
    public Photo read(Photo p) throws DAOException;
    public void update(Photo p) throws DAOException;
    public void delete(Photo p) throws DAOException;
    public List<Photo> readAll() throws DAOException;

}
