package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.time.YearMonth;
import java.io.File;
import java.util.List;

public interface ExifDAO {

    /**
     * Create the Exif-data in the data store.
     * <p>
     * The Exif-data are recorded in the data store.
     * </p>
     *
     * @param exif Exif-data which to create.
     * @return The created Exif-object
     * @throws DAOException If the data store fails to create a record.
     */
    Exif create(Exif exif) throws DAOException;

    /**
     * Update an existing exif.
     *
     * @param exif Description of the Exif-data to update together with the new values.
     * @throws DAOException If the Exif-data does not exist or the data store fails to update the record.
     */
    void update(Exif exif) throws DAOException;

    /**
     * TODO
     * Delete an existing Exif-data.
     *
     * @param exif Specifies which Exif-data to delete by providing the id.
     * @throws DAOException If the data store fails to delete the record.
     */
    void delete(Exif exif) throws DAOException;

    /**p
     * Read the exif metadata for a given photo.
     *
     * @param photo The photo for which to read the exif data.
     * @return The found exif data entity.
     * @throws DAOException if an error occurs during the read.
     */
    Exif read(Photo photo) throws DAOException;

    /**
     * Retrieve all existing Exif-data.
     *
     * @return A List of all currently known Exif-data.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    List<Exif> readAll() throws DAOException;

    /**
     * Imports the Exif-data from a photo-file
     * <p>
     * The Exif-data is read from the provided photo-files path and stored in the database
     * </p>
     *
     * @param photo The photo which Exif-data is to be stored.
     * @return The Exif-data which has been stored
     * @throws DAOException If there is no Exif-data stored in the Photo or the file does not exist.
     */
    Exif importExif(Photo photo) throws DAOException;

    /**
     * Retrieve a list of those months for which there are photos.
     *
     * @return a list of months with available photos
     */
    List<YearMonth> getMonthsWithPhotos() throws DAOException;


    /**
     * Changes the Exif-data in the file.
     * <p>
     * Reads the Exif-data from exif and writes it to the file jpegImageFile.
     * A new temporary file is created in the process and gets deleted afterwards.
     * </p>
     *
     * @param jpegImageFile File which Exif-data shall get changed
     * @param exif          The Exif-Data that is written to the file
     * @throws DAOException If there is a problem accessing the file.
     */
    void setExifTags(final File jpegImageFile, Exif exif) throws DAOException;
}
