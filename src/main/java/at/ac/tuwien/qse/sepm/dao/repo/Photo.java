package at.ac.tuwien.qse.sepm.dao.repo;

import java.nio.file.Path;

public class Photo {

    private final Path file;
    private final PhotoMetadata data;

    public Photo(Path file) {
        this(file, new PhotoMetadata());
    }

    public Photo(Path file, PhotoMetadata data) {
        if (file == null) throw new IllegalArgumentException();
        if (data == null) throw new IllegalArgumentException();
        this.file = file;
        this.data = data;
    }

    public Photo(Photo from) {
        if (from == null) throw new IllegalArgumentException();
        this.file = from.file;
        this.data = new PhotoMetadata(from.data);
    }

    public Path getFile() {
        return file;
    }

    public PhotoMetadata getData() {
        return data;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        if (!data.equals(photo.data))
            return false;
        if (!file.equals(photo.file))
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override public String toString() {
        return "Photo{" +
                "file=" + file +
                ", data=" + data +
                '}';
    }
}
