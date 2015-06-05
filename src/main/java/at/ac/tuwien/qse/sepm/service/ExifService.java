package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

public interface ExifService {

    /**
     * Returns the exif data of a photo
     *
     * @param photo the photo, must have a valid path
     * @return the exif object, containing the exif data
     * @throws ServiceException
     */
    public Exif getExif(Photo photo) throws ServiceException;

    /**
     * Reads and sets the metadata from a photo which are saved in the user comment entry in the exif data
     *
     * @param photo the photo, must have a valid path
     * @throws ServiceException
     */
    public void getTagsFromExif(Photo photo) throws ServiceException;

    /**
     * Sets the metadata from a photo to the user comment entry in the exif data of the photofile
     *
     * @param photo the photo, must have a valid path
     * @throws ServiceException
     */
    public void exportMetaToExif(Photo photo) throws ServiceException;

    /**
     * Reads date, latitude and longitude (if possible)
     *
     * @param photo the photo, must have a valid path
     * @throws ServiceException
     */
    public void attachDateAndGeoData(Photo photo) throws ServiceException;

    /**
     * Modifies the exif data of a photo
     *
     * @param photo the photo, must have a valid path
     * @throws ServiceException
     */
    public void modifyExifTags(Photo photo) throws ServiceException;
}
