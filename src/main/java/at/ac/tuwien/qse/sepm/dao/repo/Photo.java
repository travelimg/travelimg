package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;

public class Photo {

    private final Path file;

    public Photo(Path file) {
        if (file == null) throw new IllegalArgumentException();
        this.file = file;
    }

    public Path getFile() {
        return file;
    }
}
