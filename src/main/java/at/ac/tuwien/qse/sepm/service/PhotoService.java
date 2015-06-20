package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Service for manipulating and organizing photos.
 */
public interface PhotoService {

    /**
     * delete the delivered List of Photos
     *
     * @param photos the list of photos
     * @throws ServiceException
     */
    void deletePhotos(Collection<Photo> photos) throws ServiceException;

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
     * Persist the edits made to a photo.
     *
     * @param photo photo for which the changes should be stored
     * @throws ServiceException failed to perform operation
     */
    void editPhoto(Photo photo) throws ServiceException;

    /**
     * Listen for photos that have been newly added.
     *
     * @param callback callback that receives the added photos
     */
    void subscribeCreate(Consumer<Photo> callback);

    /**
     * Listen for photos that have been updated.
     *
     * @param callback callback that receives the updated photos
     */
    void subscribeUpdate(Consumer<Photo> callback);

    /**
     * Listen for photos that have been deleted.
     *
     * @param callback callback that receives the deleted photos
     */
    void subscribeDelete(Consumer<Path> callback);

    /**
     * Synchronize the photo repository asynchronously in the background and start watching for changes to the watched files.
     *
     * Should only be called once on startup.
     */
    void synchronize();
}
