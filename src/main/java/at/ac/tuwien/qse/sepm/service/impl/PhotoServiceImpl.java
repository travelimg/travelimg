package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.dao.impl.JDBCPhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class PhotoServiceImpl implements PhotoService {

    private static final Logger logger = LogManager.getLogger();

   @Autowired private PhotoDAO photoDAO;
    @Autowired private PhotoTagDAO photoTagDAO;

    public PhotoServiceImpl() {

    }

    //@Autowired
    //public void setPhotoDAO(PhotoDAO photoDAO) {
    //    this.photoDAO = photoDAO;
    //}
    //@Autowired
    //public void setPhotoTagDAO(PhotoTagDAO photoTagDAO) {
    //    this.photoTagDAO = photoTagDAO;
    //}

    public List<Photo> getAllPhotos() throws ServiceException {
        try {
            return photoDAO.readAll();
        } catch (DAOException e) {
            throw new ServiceException(e);
        } catch(ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }
    }

    public List<Tag> getAllTags() throws ServiceException {
        return null;
    }

    public void requestFullscreenMode(List<Photo> photos) throws ServiceException {

    }

    /**
     *  delete the delivered List of Photos
     * @param photos the list of photos
     * @throws ServiceException
     */
    public void deletePhotos(List<Photo> photos) throws ServiceException {
        for(Photo p : photos){
            try {
                photoDAO.delete(p);
            } catch (DAOException e) {
                throw new ServiceException(e);
            } catch (ValidationException e) {
                throw new ServiceException("Failed to validate entity", e);
            }
        }
    }

    public void addTagToPhotos(List<Photo> photos, Tag t) throws ServiceException {

    }

    /**
     * delete the delivered Tag from the delivered list of Photos
     * @param photos the list of photos
     * @param t the Tag which you want to delete
     * @throws ServiceException
     */
    public void removeTagFromPhotos(List<Photo> photos, Tag t) throws ServiceException {
        for(Photo p: photos){
            try {
                photoTagDAO.removeTagFromPhoto(t,p);
            } catch (DAOException e) {
                throw new ServiceException(e);
            } catch (ValidationException e) {
                throw new ServiceException("Failed to validate entity", e);
            }
        }

    }
}
