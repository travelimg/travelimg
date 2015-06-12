package at.ac.tuwien.qse.sepm.dao.repo;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for reading and writing photo metadata.
 */
public interface PhotoSerializer {

    /**
     * Reads a photo data from a stream.
     *
     * @param is stream containing a photo
     * @return metadata read from the stream
     * @throws PersistenceException failed to perform operation
     * @throws FormatException data in the stream is invalid
     */
    public PhotoMetadata read(InputStream is) throws PersistenceException;

    /**
     * Rewrites the meta data of a photo.
     *
     * The written data is readable with this serializer instance.
     *
     * @param is stream containing the original photo
     * @param os stream to which the updated photo should be written
     * @param metadata data that should be written
     * @throws PersistenceException failed to perform operation
     * @throws FormatException data in the input stream is invalid
     */
    public void update(InputStream is, OutputStream os, PhotoMetadata metadata) throws PersistenceException;
}
