package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Predicate;

public interface PhotoService {

    /**
     * Retrieve a list of those months for which there are photos.
     *
     * @return a list of months with available photos
     */
    List<YearMonth> getMonthsWithPhotos() throws ServiceException;

    /**
     * delete the delivered List of Photos
     *
     * @param photos the list of photos
     * @throws ServiceException
     */
    void deletePhotos(List<Photo> photos) throws ServiceException;

    /**
     * edit the delivered List of Photos
     *
     * @param photos the list of photos
     * @throws ServiceException
     */
    void editPhotos(List<Photo> photos, Photo p) throws ServiceException;

    /**
     * @return the list of all available photos
     * @throws ServiceException
     */
    List<Photo> getAllPhotos() throws ServiceException;

    /**
     * Get all photos that match the specified filter.
     *
     * @param filter filter the photos are tested against
     * @return list of all available photos that match the filter
     */
    List<Photo> getAllPhotos(Predicate<Photo> filter) throws ServiceException;

    /**
     * Persists the rating of the specified photo.
     *
     * @param photo photo for which the rating should be stored
     * @throws ServiceException failed to perform operation
     */
    void savePhotoRating(Photo photo) throws ServiceException;
}
