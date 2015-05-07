package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.List;
import java.util.function.Consumer;

public interface ImportService extends Service {

    /**
     * Import a list of photos into the application.
     *
     * The import is non-blocking and a callback is called for each photo after it is imported.
     *
     * @param photos The photos to import.
     * @param callback Called with the imported photo.
     * @param errorHandler Handler for occurring exceptions.
     */
    void importPhotos(List<Photo> photos, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler);
}
