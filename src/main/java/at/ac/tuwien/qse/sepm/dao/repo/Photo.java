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

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        if (!file.equals(photo.file))
            return false;

        return true;
    }

    @Override public int hashCode() {
        return file.hashCode();
    }

    @Override public String toString() {
        return "Photo{" +
                "file=" + file +
                '}';
    }
}
