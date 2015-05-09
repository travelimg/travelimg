package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public interface PhotoService {

    public List<Photo> getAllPhotos() throws ServiceException;
    public List<Tag> getAllTags() throws ServiceException;
    public void requestFullscreenMode(List<Photo> photos) throws ServiceException;
    public void deletePhotos(List<Photo> photos) throws ServiceException;
    public void addTagToPhotos(List<Photo> photos, Tag t) throws ServiceException;
    public void removeTagFromPhotos(List<Photo> photos, Tag t) throws ServiceException;
    public void editPhotos(List<Photo> photos, Photo p) throws ServiceException;

    /**
     * Retrieve a list of photos from a given date.
     *
     * @param date Date for which photos will be loaded.
     * @param callback Called for each matching photo
     * @param errorHandler Handler for occuring exceptions
     * @return A list of photos from the given date.
     */
    public Cancelable loadPhotosByDate(Date date, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler);

    /**
     * Retrieve a list of those months for which there are photos.
     *
     * @return a list of dates representing months with available photos
     */
    List<Date> getMonthsWithPhotos() throws ServiceException;

}
