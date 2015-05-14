package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
/**
 * @deprecated
 */
public interface ExifService {

    /**
     * Changes the Exif-data of a photo.
     *
     * The Exif-data in the file which is stored in the photos path changed and also
     * the Exif-data in the datastore is updated.
     * @param photo The photo which contains the changed Exif-data
     * @throws ServiceException If there was a problem changing the file or the database entry.
     */
    public void changeExif(Photo photo) throws ServiceException;

    /**
     * Imports Exif-data of the Photo.
     *
     * @param photo the Photo from which the Exif-data is extracted
     * @return an Exif-entity which represent the Exif database entry
     * @throws ServiceException if there is a problem with the metadata extraction process
     */
    public Exif importExif(Photo photo) throws ServiceException;
}
