package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
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
     * Retrieve an existing Tag
     *
     * @param tag Specifies which Tag to retrieve by providing the name.
     * @return the Tag-Objekt
     * @throws ServiceException If the Tag can not be retrieved or the data store fails to select the record.
     */
    Tag readName(Tag  tag) throws ServiceException;

    /**
     * Return a list of all existing tags.
     *
     * @return the list of all available tags
     * @throws ServiceException if retrieval failed
     */
    List<Tag> getAllTags() throws ServiceException;

    /**
     * Returns a list of the most used Tags, from a list of photos
     *
     * @param photos the list of photos
     * @return a list of most used tags (at most 5)
     * @throws ServiceException if retrieval failed
     */
    List<Tag> getMostFrequentTags(List<Photo> photos) throws ServiceException;

    /**
     * Add Tag <tt>tag</tt> to every photo in list <tt>photos</tt>. If a photo already has this tag,
     * then it will keep it.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag    must not be null; tag.id must not be null
     * @throws ServiceException         if an Exception in this or an underlying
     *                                  layer occurs
     */
    void addTagToPhotos(List<Photo> photos, Tag tag) throws ServiceException;

    /**
     * Remove Tag <tt>tag</tt> from all photos in list <tt>photos</tt>. If a photo in the list
     * does not have this tag, then no action will be taken for this photo.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag    must not be null; tag.id must not be null
     * @throws ServiceException         if an Exception in this or an underlying
     *                                  layer occurs
     */
    void removeTagFromPhotos(List<Photo> photos, Tag tag) throws ServiceException;

    /**
     * Return list of all tags which are currently set for <tt>photo</tt>.
     *
     * @param photo must not be null; photo.id must not be null;
     * @return List with all tags which are linked to <tt>photo</tt> as a PhotoTag;
     * If no tag exists, return an empty List.
     * @throws ServiceException         if an exception occurs on this or an underlying layer
     */
    List<Tag> getTagsForPhoto(Photo photo) throws ServiceException;
    /**  * Returns a list of the most used Tags,from a list of photos 
     * * @param l the list of photos  * @return a list of tag s , (the hot 5) 
     * * @throws ServiceException if retrieval failed 
     * */
    List<Tag> getMostWanted(List <Photo> l) throws ServiceException;
}
