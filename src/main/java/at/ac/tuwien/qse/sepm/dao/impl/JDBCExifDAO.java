package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.io.File;
import java.time.YearMonth;
import java.util.List;

/**
 * @deprecated
 */
public class JDBCExifDAO extends JDBCDAOBase implements ExifDAO {


    @Override
    public Exif create(Exif exif) throws DAOException {
        return null;
    }

    @Override
    public void update(Exif exif) throws DAOException {

    }

    @Override
    public void delete(Exif exif) throws DAOException {

    }

    @Override
    public Exif read(Photo photo) throws DAOException {
        return null;
    }

    @Override
    public List<Exif> readAll() throws DAOException {
        return null;
    }

    @Override
    public Exif importExif(Photo photo) throws DAOException {
        return null;
    }

    @Override
    public List<YearMonth> getMonthsWithPhotos() throws DAOException {
        return null;
    }

    @Override
    public void setExifTags(File jpegImageFile, Exif exif) throws DAOException {

    }
}
