package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public interface PhotoService {

    List<Photo> getAllPhotos() throws ServiceException;
    List<Tag> getAllTags() throws ServiceException;
    void requestFullscreenMode(List<Photo> photos) throws ServiceException;
    void deletePhotos(List<Photo> photos) throws ServiceException;

    /**
     * Add Tag <tt>tag</tt> to every photo in list <tt>photos</tt>. If a photo already has this tag,
     * then it will keep it.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag must not be null; tag.id must not be null
     * @throws ServiceException if an unhandled Exception in this or an underlying layer occurs
     * @throws IllegalArgumentException if any precondition is violated
     */
    void addTagToPhotos(List<Photo> photos, Tag tag) throws ServiceException;

    /**
     * Remove Tag <tt>tag</tt> from all photos in list <tt>photos</tt>. If a photo in the list
     * does not have this tag, then no action will be taken for this photo.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag must not be null; tag.id must not be null
     * @throws ServiceException if an unhandled Exception in this or an underlying layer occurs
     * @throws IllegalArgumentException if any precondition is violated
     */
    void removeTagFromPhotos(List<Photo> photos, Tag tag) throws ServiceException;

}
