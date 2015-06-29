package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service for uploading photos to a destination in the file system.
 */
public interface ExportService {

    /**
     * Fetch the dropbox base directory.
     *
     * @return The base directory of the users dropbox or null if it can't be found.
     */
    String getDropboxFolder();

    /**
     * Export a given set of photos to the specified location.
     * <p>
     * The exact folder is specified by the user and the upload happens in the background.
     *
     * @param photos       The photos to upload.
     * @param destination  The path where to the photos are exported.
     * @param callback     Callback which is called after a photo is finished exporting.
     * @param errorHandler Handler for occuring exceptions.
     * @return A cancelable task for aborting the export if desired.
     */
    Cancelable exportPhotos(Collection<Photo> photos, String destination, Consumer<Photo> callback,
            ErrorHandler<ServiceException> errorHandler);
}
