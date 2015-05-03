package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.impl.JDBCPhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class ImportServiceImpl implements ImportService {

    private static final Logger logger = LogManager.getLogger();

    private PhotoDAO photoDAO;

    public ImportServiceImpl(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    public void importPhotos(List<Photo> photos) throws ServiceException {
        for(Photo p: photos){
            try {
                photoDAO.create(p);
            } catch (DAOException e) {
                throw new ServiceException(e.getMessage());
            }
        }
    }

    public void addPhotographerToPhotos(List<Photo> photos, Photographer photographer) throws ServiceException {

    }

    public void editPhotographerForPhotos(List<Photo> photos, Photographer photographer) throws ServiceException {

    }
}
