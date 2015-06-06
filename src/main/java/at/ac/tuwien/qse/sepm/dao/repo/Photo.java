package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.entities.Rating;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Photo {

    private final Path file;

    private LocalDateTime date = LocalDateTime.now();
    private double longitude = 0.0;
    private double latitude = 0.0;
    private double altitude = 0.0;
    private Rating rating = Rating.NONE;
    private String photographer = "";
    private Set<String> tags = new HashSet<>();

    public Photo(Path file) {
        if (file == null) throw new IllegalArgumentException();
        this.file = file;
    }

    public Photo(Photo from) {
        if (from == null) throw new IllegalArgumentException();
        this.file = from.file;
        this.date = LocalDateTime.from(from.date);
        this.longitude = from.longitude;
        this.latitude = from.latitude;
        this.altitude = from.altitude;
        this.rating = from.rating;
        this.photographer = from.photographer;
        this.tags.addAll(from.getTags());
    }

    public Path getFile() {
        return file;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        if (date == null) throw new IllegalArgumentException();
        this.date = date;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        if (rating == null) throw new IllegalArgumentException();
        this.rating = rating;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        if (photographer == null) throw new IllegalArgumentException();
        this.photographer = photographer;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        if (Double.compare(photo.altitude, altitude) != 0)
            return false;
        if (Double.compare(photo.latitude, latitude) != 0)
            return false;
        if (Double.compare(photo.longitude, longitude) != 0)
            return false;
        if (!date.equals(photo.date))
            return false;
        if (!file.equals(photo.file))
            return false;
        if (!photographer.equals(photo.photographer))
            return false;
        if (rating != photo.rating)
            return false;
        if (!tags.equals(photo.tags))
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = file.hashCode();
        result = 31 * result + date.hashCode();
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + rating.hashCode();
        result = 31 * result + photographer.hashCode();
        result = 31 * result + tags.hashCode();
        return result;
    }

    @Override public String toString() {
        return "Photo{" +
                "file=" + file +
                ", date=" + date +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", rating=" + rating +
                ", photographer='" + photographer + '\'' +
                ", tags=" + tags +
                '}';
    }
}
