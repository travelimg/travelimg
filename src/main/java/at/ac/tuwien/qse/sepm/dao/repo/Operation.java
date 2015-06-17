package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;

/**
 * Describes an operation on a specific file.
 */
public interface Operation {

    /**
     * Get the photo file on which the operation is performed.
     *
     * @return file of photo
     */
    Path getFile();

    /**
     * Get the kind of operation that is performed.
     *
     * @return value describing what happens with the file
     */
    Kind getKind();

    public enum Kind {
        READ,
        CREATE,
        UPDATE,
        DELETE,
    }
}
