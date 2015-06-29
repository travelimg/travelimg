package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

/**
 * Service for reading and changing exif data.
 */
public interface ExifService {

    /**
     * Returns the exif data of a photo
     *
     * @param photo the photo, must have a valid path
     * @return the exif object, containing the exif data
     * @throws ServiceException
     */
    Exif getExif(Photo photo) throws ServiceException;

    /**
     * Save date, geodata, tags, place and journey for a given photo
     * @param photo the photo, must have a valid path
     * @throws ServiceException
     */
    void setMetaData(Photo photo) throws ServiceException;
}
