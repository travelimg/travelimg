package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service for uploading photos to dropbox
 */
public interface DropboxService extends Service {

    /**
     * Fetch the dropbox base directory.
     *
     * @return The base directory of the users dropbox.
     */
    String getDropboxFolder() throws ServiceException;

    /**
     * Upload a given set of photos to the user dropbox.
     * <p>
     * The exact folder is specified by the user and the upload happens in the background.
     *
     * @param photos       The photos to upload.
     * @param destination  The path relative to the dropbox base where to the photos are uploaded.
     * @param callback     Callback which is called after a photo is finished uploading.
     * @param errorHandler Handler for occuring exceptions.
     * @return A cancelable task for aborting the upload if desired.
     */
    Cancelable uploadPhotos(List<Photo> photos, String destination, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler);
}
