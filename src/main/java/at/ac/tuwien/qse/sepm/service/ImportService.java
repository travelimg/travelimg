package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public void importPhotos(List<Photo> photos, Consumer<Photo> callback, ErrorHandler<ServiceException> errorHandler);
    public void addPhotographerToPhotos(List<Photo> photos, Photographer photographer) throws ServiceException;
    public void editPhotographerForPhotos(List<Photo> photos, Photographer photographer) throws ServiceException;
}
