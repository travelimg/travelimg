package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;
import java.util.Collection;

/**
 * A source from which photos can be read.
 */
public interface PhotoProvider {

    /**
     * Get meta information for a single photo.
     *
     * @param file path of the photo that should be checked
     * @return meta information for the photo, or {@code null} if it does not exist
     * @throws PersistenceException failed to perform operation
     */
    PhotoInfo check(Path file) throws PersistenceException;

    /**
     * Get meta information for all photos.
     *
     * @return meta information for all photos
     * @throws PersistenceException failed to perform operation
     */
    Collection<PhotoInfo> checkAll() throws PersistenceException;

    /**
     * Read a single photo.
     *
     * @param file path of the photo that should be read
     * @return photo that was read
     * @throws PersistenceException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the provider
     */
    Photo read(Path file) throws PersistenceException;

    /**
     * Read all photos.
     *
     * @return all photos in the provider
     * @throws PersistenceException failed to perform operation
     */
    Collection<Photo> readAll() throws PersistenceException;
}
