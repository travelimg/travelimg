package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface TagDAO {

    /**
     * Create a Tag in the data store
     *
     * @param t Tag which to create
     * @return the created Tag
     * @throws DAOException If the Tag can not be created or the data store fails to create a record.
     */
    Tag create(Tag t) throws DAOException, ValidationException;

    /**
     * Retrieve an existing Tag
     *
     * @param t Specifies which Tag to retrive by providing the id.
     * @return the Tag-Objekt
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    Tag read(Tag t) throws DAOException;

    /**
     * Retrieve an existing Tag
     *
     * @param t Specifies which Tag to retrieve by providing the name.
     * @return the Tag-Objekt
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    Tag readName(Tag t) throws DAOException;

    /**
     * Check if a tag already exists.
     * @param tag The tag to be checked.
     * @return true if a tag with the same name already exists else false.
     * @throws DAOException If the data store fails to select the record.
     */
    boolean exists(Tag tag) throws DAOException;

    /**
     * Delete an existing Tag.
     *
     * @param t Specifies which Tag to delete by providing the id.
     * @throws DAOException If the Tag can not be deleted or the data store fails to delete the record.
     */
    void delete(Tag t) throws DAOException;

    /**
     * Retrieve a list of all existing Tags
     *
     * @return the list of Tags
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    List<Tag> readAll() throws DAOException;

}
