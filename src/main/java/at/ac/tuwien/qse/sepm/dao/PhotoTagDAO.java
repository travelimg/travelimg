package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public interface PhotoTagDAO {

    /**
     * Create a persistent photo-tag entry that links Tag <tt>tag</tt> to Photo <tt>photo</tt>.
     * If an equal entry already exists, nothing happens.
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException: if an exception occurs on persistence layer
     * @throws IllegalArgumentException: if any precondition is violated
     */
    void createPhotoTag(Photo photo, Tag tag) throws DAOException;

    /**
     * Remove if exists the photo-tag entry where Photo = <tt>photo</tt> and Tag = <tt>tag</tt>
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException: if an exception occurs on persistence layer
     * @throws IllegalArgumentException: if any precondition is violated
     */
    void removeTagFromPhoto(Photo photo, Tag tag) throws DAOException;

    List<Tag> readTagsByPhoto(Photo photo) throws DAOException;

    List<Photo> readPhotosByTag(Tag tag) throws DAOException;
}
