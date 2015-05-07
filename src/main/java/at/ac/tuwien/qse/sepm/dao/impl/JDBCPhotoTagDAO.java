package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class JDBCPhotoTagDAO extends JDBCDAOBase implements PhotoTagDAO {

    private static final Logger logger = LogManager.getLogger();

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
