package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.impl.JDBCPhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import java.util.List;

public class ImportServiceImpl implements ImportService {

    private PhotoDAO photoDAO;

    public ImportServiceImpl() throws ServiceException{
        photoDAO = new JDBCPhotoDAO();

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
