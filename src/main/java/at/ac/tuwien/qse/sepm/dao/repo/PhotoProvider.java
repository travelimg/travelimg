package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A source from which photos can be read.
 */
public interface PhotoProvider {

    /**
     * Get all photo files in the provider.
     *
     * @return collection of files that can be read
     * @throws DAOException failed to perform operation
     */
    Collection<Path> index() throws DAOException;

    /**
     * Checks whether a provider contains a certain photo.
     *
     * @param file path of the photo that should be checked
     * @return true if the provider contains the photo, otherwise false
     * @throws DAOException failed to perform operation
     */
    default boolean contains(Path file) throws DAOException {
        return index().contains(file);
    }

    /**
     * Read a single photo.
     *
     * @param file path of the photo that should be read
     * @return photo that was read
     * @throws DAOException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the provider
     */
    Photo read(Path file) throws DAOException;

    /**
     * Read all photos.
     *
     * @return all photos in the provider
     * @throws DAOException failed to perform operation
     */
    default Collection<Photo> readAll() throws DAOException {
        Collection<Path> index = index();
        Collection<Photo> result = new ArrayList<>(index.size());
        for (Path file : index) {
            result.add(read(file));
        }
        return result;
    }
}
