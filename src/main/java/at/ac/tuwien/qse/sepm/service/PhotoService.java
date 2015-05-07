package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public interface PhotoService {

    public List<Photo> getAllPhotos() throws ServiceException;
    public List<Tag> getAllTags() throws ServiceException;
    public void requestFullscreenMode(List<Photo> photos) throws ServiceException;
    public void deletePhotos(List<Photo> photos) throws ServiceException;
    public void addTagToPhotos(List<Photo> photos, Tag t) throws ServiceException;
    public void removeTagFromPhotos(List<Photo> photos, Tag t) throws ServiceException;
    public void editPhotos(List<Photo> photos, Photo p) throws ServiceException;

}
