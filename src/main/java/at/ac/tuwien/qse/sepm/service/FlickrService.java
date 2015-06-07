package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;

import java.util.function.Consumer;

public interface FlickrService extends Service {

    /**
     * Downloads (if possible) 10 new photos every time it is called. This is a non-blocking operation
     *
     * @param tags a list of tags used as keywords inorder to perform the search
     * @param latitude used to find photos near to it
     * @param longitude used to find photos near to ii
     * @param useGeoData if true, geodata(latitude and longitude) will be used for searching
     * @param callback used to notify the GUI after a new photo has been downloaded(the photo object will contain the path where the photo is temporary stored)
     * @param errorHandler handler for occurring exceptions
     * @return a Cancelable object, that can be used to interrupt the download
     * @throws ServiceException
     */
    public Cancelable downloadPhotos(String tags[],double latitude, double longitude, boolean useGeoData, Consumer<Photo> callback, Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) throws ServiceException;

    /**
     * After calling this method, the service will be ready to load photos again from the beginning.
     */
    public void reset();

}
