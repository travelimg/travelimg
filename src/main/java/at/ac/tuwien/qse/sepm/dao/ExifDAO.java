package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Exif;

import java.util.List;

public interface ExifDAO {
    public Exif create(Exif e) throws DAOException;
    public Exif read(Exif e) throws DAOException;
    public void update(Exif e) throws DAOException;
    public void delete(Exif e) throws DAOException;
    public List<Exif> readAll() throws DAOException;
}
