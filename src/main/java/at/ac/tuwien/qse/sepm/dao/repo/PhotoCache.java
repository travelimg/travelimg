package at.ac.tuwien.qse.sepm.dao.repo;

/**
 * Storage for photo instances.
 */
public interface PhotoCache extends PhotoProvider {

    /**
     * Puts a photo into the cache, replacing it if it already exists.
     *
     * The photo can be read immediately after the method returns.
     *
     * @param photo photo that should be put into the cache
     * @throws PersistenceException failed to perform operation
     */
    void put(Photo photo) throws PersistenceException;

    /**
     * Puts a photo into the cache, replacing it if it already exists.
     *
     * The photo can be read immediately after the method returns.
     *
     * @param photo photo that should be put into the cache
     * @throws PersistenceException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the cache
     */
    void remove(Photo photo) throws PersistenceException;
}
