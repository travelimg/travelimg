package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.entities.Rating;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Context {

    private final Path basePath;

    public Context(Path basePath) {
        this.basePath = basePath;
    }

    public Path getFile1() {
        return basePath.resolve("india/food.jpg");
    }

    public Path getFile2() {
        return basePath.resolve("usa/grand-canyon.jpg");
    }

    public Photo applyData1(Photo photo) {
        photo.setDate(LocalDateTime.of(1993, 3, 30, 7, 5));
        photo.setLongitude(17);
        photo.setLatitude(18);
        photo.setAltitude(19);
        photo.setRating(Rating.GOOD);
        photo.setPhotographer("Kris");
        photo.getTags().add("food");
        photo.getTags().add("india");
        return photo;
    }

    public Photo applyData2(Photo photo) {
        photo.setDate(LocalDateTime.of(2014, 8, 14, 15, 36));
        photo.setLongitude(42);
        photo.setLatitude(43);
        photo.setAltitude(44);
        photo.setRating(Rating.NEUTRAL);
        photo.setPhotographer("Lukas");
        photo.getTags().add("usa");
        photo.getTags().add("nature");
        return photo;
    }

    public Photo getPhoto1() {
        return applyData1(new Photo(getFile1()));
    }

    public Photo getPhoto2() {
        return applyData2(new Photo(getFile2()));
    }

    public Photo getPhoto1Modified() {
        return applyData2(new Photo(getFile1()));
    }

    public Photo getPhoto2Modified() {
        return applyData1(new Photo(getFile2()));
    }

    public InputStream getStream1() {
        return new ByteArrayInputStream(new byte[] { 1 });
    }

    public InputStream getStream2() {
        return new ByteArrayInputStream(new byte[] { 2 });
    }

    public Photo read(Path file, InputStream stream) throws IOException {
        int index = stream.read();
        switch (index) {
            case 1 : return applyData1(new Photo(file));
            case 2 : return applyData2(new Photo(file));
            default : throw new IOException("Invalid format.");
        }
    }

    public void update(Photo photo, OutputStream stream) throws IOException {
        // Just use the rating to map photo to stream content.
        switch (photo.getRating()) {
            case GOOD :
                stream.write(1);
                break;
            case NEUTRAL :
                stream.write(2);
                break;
            default :
                stream.write(0);
                break;
        }
    }
}
