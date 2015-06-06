package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class PhotoInfo {

    private final Path file;
    private final LocalDateTime modified;

    public PhotoInfo(Path file, LocalDateTime modified) {
        if (file == null) throw new IllegalArgumentException();
        if (modified == null) throw new IllegalArgumentException();
        this.file = file;
        this.modified = modified;
    }

    public Path getFile() {
        return file;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    @Override public String toString() {
        return "PhotoInfo{" +
                "file=" + file +
                ", modified=" + modified +
                '}';
    }
}
