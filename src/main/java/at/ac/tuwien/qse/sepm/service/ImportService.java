package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service for importing external photos into the users library.
 */
public interface ImportService {

    /**
     * Import a list of photos into the application.
     * <p>
     * The import is non-blocking and a callback is called for each photo after it is imported.
     *
     * @param photos       The photos to import.
     * @param callback     Called with the imported photo.
     * @param errorHandler Handler for occurring exceptions.
     */
    Cancelable importPhotos(List<Photo> photos, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler);
}
