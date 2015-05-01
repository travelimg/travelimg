package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public interface PhotoTagDAO {

    public void createPhotoTag(Photo p, Tag t) throws DAOException;
    public void removeTagFromPhoto(Tag t, Photo p) throws DAOException;
    public List<Tag> readTagsByPhoto(Photo p) throws DAOException;
    public List<Photo> readPhotosByTag(Tag t) throws DAOException;
}
