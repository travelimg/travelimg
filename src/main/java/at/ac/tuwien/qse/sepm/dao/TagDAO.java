package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public interface TagDAO {

    /**
     * Create a Tag in the data store
     *
     * @param t Tag which to create
     * @return the created Tag
     * @throws DAOException If the Tag can not be copied or the data store fails to create a record.
     */
    public Tag create(Tag t) throws DAOException;

    /**
     * Retrieve an existing Tag
     *
     * @param t Specifies which Tag to retrive by providing the id.
     * @return the Tag-Objekt
     * @throws DAOException If the Tag can not be retrived or the data store fails to select the record.
     */
    public Tag read(Tag t) throws DAOException;


    /**
     * Delete an existing Tag.
     *
     * @param t Specifies which Tag to delete by providing the id.
     * @throws DAOException If the Tag can not be deleted or the data store fails to delete the record.
     */
    public void delete(Tag t) throws DAOException;

    /**
     * Retrieve a list of all existing Tags
     *
     * @return the list of Tags
     * @throws DAOException If the Tag can not be retrived or the data store fails to select the record.
     */
    public List<Tag> readAll() throws DAOException;

}
