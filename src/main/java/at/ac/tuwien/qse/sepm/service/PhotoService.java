package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Consumer;

public interface PhotoService {

    public List<Photo> getAllPhotos() throws ServiceException;
    public List<Tag> getAllTags() throws ServiceException;
    public void requestFullscreenMode(List<Photo> photos) throws ServiceException;
    public void deletePhotos(List<Photo> photos) throws ServiceException;
    public void editPhotos(List<Photo> photos, Photo p) throws ServiceException;

    /**
     * Retrieve a list of photos from a given month.
     *
     * @param month Month for which photos will be loaded.
     * @param callback Called for each matching photo
     * @param errorHandler Handler for occuring exceptions
     * @return A list of photos from the given date.
     */
    Cancelable loadPhotosByMonth(YearMonth month, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler);

    /**
     * Retrieve a list of those months for which there are photos.
     *
     * @return a list of months with available photos
     */
    List<YearMonth> getMonthsWithPhotos() throws ServiceException;

    /**
     * Add Tag <tt>tag</tt> to every photo in list <tt>photos</tt>. If a photo already has this tag,
     * then it will keep it.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag must not be null; tag.id must not be null
     * @throws ServiceException if an unhandled Exception in this or an underlying layer occurs
     * @throws IllegalArgumentException if any precondition is violated
     */
    void addTagToPhotos(List<Photo> photos, Tag tag) throws ServiceException;

    /**
     * Remove Tag <tt>tag</tt> from all photos in list <tt>photos</tt>. If a photo in the list
     * does not have this tag, then no action will be taken for this photo.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag must not be null; tag.id must not be null
     * @throws ServiceException if an unhandled Exception in this or an underlying layer occurs
     * @throws IllegalArgumentException if any precondition is violated
     */
    void removeTagFromPhotos(List<Photo> photos, Tag tag) throws ServiceException;

    /**
     * Return list of all tags which are currently set for <tt>photo</tt>.
     *
     * @param photo must not be null; photo.id must not be null;
     * @return List with all tags which are linked to <tt>photo</tt> as a PhotoTag;
     *     If no tag exists, return an empty List.
     * @throws ServiceException if an exception occurs on this or an underlying layer
     * @throws IllegalArgumentException if any precondition is violated
     */
    List<Tag> getTagsForPhoto(Photo photo) throws ServiceException;

    /**
     * Persists the rating of the specified photo.
     *
     * @param photo photo for which the rating should be stored
     * @throws ServiceException failed to perform operation
     */
    void savePhotoRating(Photo photo) throws ServiceException;
}
