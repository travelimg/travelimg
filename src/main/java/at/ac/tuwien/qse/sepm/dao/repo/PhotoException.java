package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;

/**
 * Exception that is thrown when an operation on a specific photo failed.
 */
public abstract class PhotoException extends PersistenceException {

    private final PhotoProvider photoProvider;
    private final Path photoFile;

    public PhotoException(PhotoProvider photoProvider, Path photoFile) {
        if (photoProvider == null) throw new IllegalArgumentException();
        if (photoFile == null) throw new IllegalArgumentException();
        this.photoProvider = photoProvider;
        this.photoFile = photoFile;
    }

    public PhotoException(PhotoProvider photoProvider, Path photoFile, Throwable cause) {
        super(cause);
        if (photoProvider == null) throw new IllegalArgumentException();
        if (photoFile == null) throw new IllegalArgumentException();
        this.photoProvider = photoProvider;
        this.photoFile = photoFile;
    }

    /**
     * Get the provider in which the operation was performed.
     *
     * @return provider responsible for the error
     */
    public PhotoProvider getPhotoProvider() {
        return photoProvider;
    }

    /**
     * Get the path of the photo for which the operation failed.
     *
     * @return path of photo
     */
    public Path getPhotoFile() {
        return photoFile;
    }
}
