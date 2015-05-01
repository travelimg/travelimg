package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import java.util.List;

public class PhotoServiceImpl implements PhotoService {

    public List<Photo> getAllPhotos() throws ServiceException {
        return null;
    }

    public List<Tag> getAllTags() throws ServiceException {
        return null;
    }

    public void requestFullscreenMode(List<Photo> photos) throws ServiceException {

    }

    public void deletePhotos(List<Photo> photos) throws ServiceException {

    }

    public void addTagToPhotos(List<Photo> photos, Tag t) throws ServiceException {

    }

    public void removeTagFromPhotos(List<Photo> photos, Tag t) throws ServiceException {

    }
}
