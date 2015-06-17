package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public interface PhotoDAO {

    /**
     * Create the photo in the data store.
     *
     * @param photo Photo which to create.
     * @return The created photo
     * @throws DAOException If the store fails to create a record.
     */
    Photo create(Photo photo) throws DAOException;

    /**
     * Update an existing photo.
     *
     * @param photo Description of the photo to update together with the new values.
     * @throws DAOException If the photo does not exist or the data store fails to update the record.
     */
    void update(Photo photo) throws DAOException;

    /**
     * Delete an existing photo.
     *
     * @param photo Specifies which photo to delete by providing the id.
     * @throws DAOException If the data store fails to delete the record.
     */
    void delete(Photo photo) throws DAOException;

    /**
     * Get a photo by its id
     *
     * @param id the id of the photo
     * @return The photo belonging to the id
     * @throws DAOException        If the photo can not be retrived.
     */
    Photo getById(int id) throws DAOException;

    /**
     * Get a photo by its path.
     *
     * @param file The file of the desired photo.
     * @return The photo belonging to the given path.
     * @throws DAOException If no photo exists under this path.
     */
    Photo getByFile(Path file) throws DAOException;

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

    /**
     * Retrieve a list of photos from a given time interval.
     *
     * @param start lower bound for photo time.
     * @param end upper bound for photo time.
     * @return a list of photos falling into the time interval.
     * @throws DAOException if retrieval fails due to a datastore error.
     */
    List<Photo> readPhotosBetween(LocalDateTime start, LocalDateTime end) throws DAOException;
}
