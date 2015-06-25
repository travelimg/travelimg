package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;

/**
 * Exception that is thrown when creating a photo that already exists in a repository.
 */
public class PhotoAlreadyExistsException extends PhotoException {

    public PhotoAlreadyExistsException(PhotoProvider photoProvider, Path photoFile) {
        super(photoProvider, photoFile);
    }

    public PhotoAlreadyExistsException(PhotoProvider photoProvider, Path photoFile,
            Throwable cause) {
        super(photoProvider, photoFile, cause);
    }
}
