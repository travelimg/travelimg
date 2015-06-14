package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;

public interface PhotoDAO {

    /**
     * Create the photo in the data store.
     * <p>
     * The given photo is copied to the image folder and the attributes are recorded in the data store. The exif data is
     * additionally also parsed and stored.
     *
     * @param photo Photo which to create.
     * @return The created photo
     * @throws DAOException If the photo can not be copied or the data store fails to create a record.
     */
    Photo create(Photo photo) throws DAOException, ValidationException;

    /**
     * Update an existing photo.
     *
     * @param photo Description of the photo to update together with the new values.
     * @throws DAOException If the photo does not exist or the data store fails to update the record.
     */
    void update(Photo photo) throws DAOException, ValidationException;

    /**
     * Delete an existing photo.
     * <p>
     * The corresponding image file is deleted from the image folder.
     *
     * @param photo Specifies which photo to delete by providing the id.
     * @throws DAOException If the photo file can not be deleted or the data store fails to delete the record.
     */
    void delete(Photo photo) throws DAOException, ValidationException;

    /**
     * Get a photo by its id
     *
     * @param id the id of the photo
     * @return The photo belonging to the id
     * @throws DAOException        If the photo can not be retrived.
     * @throws ValidationException If the id is invalid.
     */
    Photo getById(int id) throws DAOException, ValidationException;

    /**
     * Get a photo by its filepath.
     *
     * @param file The file of the desired photo.
     * @return The photo belonging to the given path.
     * @throws DAOException If no photo exists under this path.
     * @throws ValidationException If the given file is null.
     */
    Photo getByFile(Path file) throws DAOException, ValidationException;

    /**
     * Retrieve all existing photos.
     *
     * @return A List of all currently known photos.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    List<Photo> readAll() throws DAOException;

    /**
     * Retrieve a list of photos from a given journey
     *
     * @param journey this sets the start and end-date
     * @return the list with the photos
     */
    List<Photo> readPhotosByJourney(Journey journey) throws DAOException;

}
