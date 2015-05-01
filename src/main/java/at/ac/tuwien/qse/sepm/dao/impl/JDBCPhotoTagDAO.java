package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public class JDBCPhotoTagDAO implements PhotoTagDAO {
    public void createPhotoTag(Photo p, Tag t) throws DAOException {

    }

    public void removeTagFromPhoto(Tag t, Photo p) throws DAOException {

    }

    public List<Tag> readTagsByPhoto(Photo p) throws DAOException {
        return null;
    }

    public List<Photo> readPhotosByTag(Tag t) throws DAOException {
        return null;
    }
}
