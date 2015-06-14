package at.ac.tuwien.qse.sepm.entities;

import at.ac.tuwien.qse.sepm.dao.repo.PhotoMetadata;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;

public class Photo {

    private Integer id;
    private Path file = Paths.get("");
    private final PhotoMetadata data;

    public Photo() {
        data = new PhotoMetadata();
    }

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
        this.id = from.id;
        this.file = from.file;
        this.data = new PhotoMetadata(from.data);
    }

    @Deprecated
    public Photo(Integer id, Photographer photographer, String path, Rating rating, LocalDateTime datetime, double latitude, double longitude, Place place) {
        this(Paths.get(path));
        setId(id);
        setPhotographer(photographer);
        setRating(rating);
        setDatetime(datetime);
        setLatitude(latitude);
        setLongitude(longitude);
        setPlace(place);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Path getFile() {
        return file;
    }

    public PhotoMetadata getData() {
        return data;
    }

    @Deprecated
    public Place getPlace() {
        return getData().getPlace();
    }

    @Deprecated
    public void setPlace(Place place) {
        getData().setPlace(place);
    }

    @Deprecated
    public String getPath() {
        return file.toString();
    }

    @Deprecated
    public void setPath(String path) {
        this.file = Paths.get(path);
    }

    @Deprecated
    public Rating getRating() {
        return getData().getRating();
    }

    @Deprecated
    public void setRating(Rating rating) {
        getData().setRating(rating);
    }

    @Deprecated
    public Photographer getPhotographer() {
        return getData().getPhotographer();
    }

    @Deprecated
    public void setPhotographer(Photographer photographer) {
        getData().setPhotographer(photographer);
    }

    @Deprecated
    public LocalDateTime getDatetime() {
        return getData().getDate();
    }

    @Deprecated
    public void setDatetime(LocalDateTime datetime) {
        getData().setDate(datetime);
    }

    @Deprecated
    public double getLatitude() {
        return getData().getLatitude();
    }

    @Deprecated
    public void setLatitude(double latitude) {
        getData().setLatitude(latitude);
    }

    @Deprecated
    public double getLongitude() {
        return getData().getLongitude();
    }

    @Deprecated
    public void setLongitude(double longitude) {
        getData().setLongitude(longitude);
    }

    @Deprecated
    public Set<Tag> getTags() {
        return getData().getTags();
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        if (id != photo.id)
            return false;
        if (data != null ? !data.equals(photo.data) : photo.data != null)
            return false;
        if (file != null ? !file.equals(photo.file) : photo.file != null)
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = id;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Photo{" +
                "id=" + id +
                ", file=" + file +
                ", data=" + data +
                '}';
    }
}
