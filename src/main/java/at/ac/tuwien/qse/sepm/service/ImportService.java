package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;

import java.util.List;

public interface ImportService {

    public void importPhotos(List<Photo> photos) throws ServiceException;
    public void addPhotographerToPhotos(List<Photo> photos, Photographer photographer) throws ServiceException;
    public void editPhotographerForPhotos(List<Photo> photos, Photographer photographer) throws ServiceException;
}
