package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import com.flickr4java.flickr.photos.Photo;

import java.util.List;
import java.util.function.Consumer;

/**
 * Service for accessing and downloading photos from flickr.
 */
public interface FlickrService {

    /**
     * Downloads (if possible) 10 new photos(in medium size) every time it is called. This is a non-blocking operation
     *
     * @param tags         a list of tags used as keywords inorder to perform the search
     * @param latitude     used to find photos near to it
     * @param longitude    used to find photos near to ii
     * @param useGeoData   if true, geodata(latitude and longitude) will be used for searching
     * @param callback     used to notify the GUI after a new photo has been downloaded
     * @param progressCallback used to notify the GUI about the download progress
     * @param errorHandler handler for occurring exceptions
     * @return a Cancelable object, that can be used to interrupt the download
     * @throws ServiceException
     */
    Cancelable searchPhotos(String tags[], double latitude, double longitude, boolean useGeoData,
            Consumer<Photo> callback, Consumer<Double> progressCallback,
            ErrorHandler<ServiceException> errorHandler) throws ServiceException;

    /**
     * Downloads photos from flickr in original size
     * @param photos the photos, should contain the url as path, geodata and date
     * @param callback used to notify the GUI after a new photo has been downloaded
     * @param progressCallback used to notify the GUI about the download progress
     * @param errorHandler handler for occurring exceptions
     * @return a Cancelable object, that can be used to interrupt the download
     * @throws ServiceException
     */
    Cancelable downloadPhotos(List<Photo> photos, Consumer<Photo> callback, Consumer<Double> progressCallback,
            ErrorHandler<ServiceException> errorHandler) throws ServiceException;

    /**
     * After calling this method, the service will be ready to load photos again from the beginning.
     */
    void reset();

    /**
     * Cleanup used resources.
     */
    void close();
}
