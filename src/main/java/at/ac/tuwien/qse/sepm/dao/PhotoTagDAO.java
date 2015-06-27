package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface PhotoTagDAO {

    /**
     * Create a persistent photo-tag entry that links Tag <tt>tag</tt> to Photo <tt>photo</tt>.
     * If an equal entry already exists, nothing happens.
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void createPhotoTag(Photo photo, Tag tag) throws DAOException, ValidationException;

    /**
     * Remove if exists the photo-tag entry where Photo = <tt>photo</tt> and Tag = <tt>tag</tt>
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void removeTagFromPhoto(Photo photo, Tag tag) throws DAOException, ValidationException;

    /**
     * Delete if existent all photo-tag entries where Tag = <tt>tag</tt>
     *
     * @param tag must not be null; tag.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void deleteAllEntriesOfSpecificTag(Tag tag) throws DAOException, ValidationException;

    /**
     * Delete if existent all photo-tag entries where Photo = <tt>photo</tt>
     *
     * @param photo must not be null; photo.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void deleteAllEntriesOfSpecificPhoto(Photo photo) throws DAOException;

    /**
     * Return list of all tags which are currently set for <tt>photo</tt>.
     *
     * @param photo must not be null; photo.id must not be null;
     * @return List with all tags which are linked to <tt>photo</tt> as a PhotoTag;
     * If no tag exists, return an empty List.
     * @throws DAOException         if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    List<Tag> readTagsByPhoto(Photo photo) throws DAOException, ValidationException;
}
