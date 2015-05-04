package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.dao.impl.JDBCExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

public class ExifServiceImpl implements ExifService{
    private ExifDAO exifDAO;

    public ExifServiceImpl() throws ServiceException {
        try {
            exifDAO = new JDBCExifDAO();
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }

    }

    public void changeExif(Exif e) {

    }

    public void importExif(Photo p) {

    }
}
