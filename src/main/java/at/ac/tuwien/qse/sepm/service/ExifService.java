package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

public interface ExifService {
    public void changeExif(Photo p) throws ServiceException;

    /**
     *  Imports Exif-data of the Photo.
     * @param p the Photo from which the Exif-data is extracted
     * @return an Exif-entity which represent the Exif database entry
     * @throws ServiceException if there is a problem with the metadata extraction process
     */
    public Exif importExif(Photo p) throws ServiceException;
}
