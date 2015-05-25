package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public interface TagService extends Service {

    /**
     * Create a Tag in the data store.
     *
     * @param tag Tag which to create; must not be null; must not already have an id
     * @return the created Tag
     * @throws ServiceException If the Tag can not be created or the data store fails to
     *      create a record.
     */
    Tag create(Tag tag) throws ServiceException;


    /**
     * Delete an existing Tag.
     *
     * @param tag Specifies which Tag to delete by providing the id;
     *            must not be null;
     *            <tt>tag.id</tt> must not be null;
     * @throws ServiceException If the Tag can not be deleted or the data store fails to
     *     delete the record.
     */
    void delete(Tag tag) throws ServiceException;

    /**
     * Return a list of all existing tags.
     *
     * @return the list of all available tags
     * @throws ServiceException if retrieval failed
     */
    List<Tag> getAllTags() throws ServiceException;
}
