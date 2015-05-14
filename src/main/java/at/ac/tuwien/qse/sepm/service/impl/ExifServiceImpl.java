package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @deprecated
 */
public class ExifServiceImpl implements ExifService {

    private static final Logger logger = LogManager.getLogger(ExifServiceImpl.class);
    private ExifDAO exifDAO;

    public ExifServiceImpl() {

    }

    @Autowired public void setExifDAO(ExifDAO exifDAO) {
        this.exifDAO = exifDAO;
    }

    public void changeExif(Photo photo) throws ServiceException {
        File file = new File(photo.getPath());
        Exif exif = photo.getExif();

        try {
            exifDAO.setExifTags(file, exif);
            exifDAO.update(exif);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public Exif importExif(Photo photo) throws ServiceException {
        try {
            return exifDAO.importExif(photo);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
