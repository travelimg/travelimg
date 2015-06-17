package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;

public class PhotoNotFoundException extends PhotoException {

    public PhotoNotFoundException(PhotoProvider photoProvider, Path photoFile) {
        super(photoProvider, photoFile);
    }

    public PhotoNotFoundException(PhotoProvider photoProvider, Path photoFile, Throwable cause) {
        super(photoProvider, photoFile, cause);
    }
}
