package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.util.List;

public interface PhotoDAO {

    /**
     * Create the photo in the data store.
     *
     * The given photo is copied to the image folder and the attributes are recorded in the data store.
     *
     * @param photo Photo which to create.
     * @return The created photo
     * @throws DAOException If the photo can not be copied or the data store fails to create a record.
     */
    public Photo create(Photo photo) throws DAOException;

    /**
     * Update an existing photo.
     *
     * @param photo Description of the photo to update together with the new values.
     *
     * @throws DAOException If the photo does not exist or the data store fails to update the record.
     */
    public void update(Photo photo) throws DAOException;

    /**
     * Delete an existing photo.
     *
     * The corresponding image file is deleted from the image folder.
     *
     * @param photo Specifies which photo to delete by providing the id.
     * @throws DAOException If the photo file can not be deleted or the data store fails to delete the record.
     */
    public void delete(Photo photo) throws DAOException;

    /**
     * Retrieve all existing photos.
     *
     * @return A List of all currently known photos.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    public List<Photo> readAll() throws DAOException;

}
