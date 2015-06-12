package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.entities.Rating;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PhotoMetadata {

    private LocalDateTime date = null;
    private Double longitude = null;
    private Double latitude = null;
    private Rating rating = Rating.NONE;
    private String photographer = null;
    private Set<String> tags = new HashSet<>();

    public PhotoMetadata() {
    }

    public PhotoMetadata(PhotoMetadata from) {
        if (from == null) throw new IllegalArgumentException();
        this.date = LocalDateTime.from(from.date);
        this.longitude = from.longitude;
        this.latitude = from.latitude;
        this.rating = from.rating;
        this.photographer = from.photographer;
        this.tags.addAll(from.getTags());
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
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
        this.photographer = photographer;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PhotoMetadata))
            return false;

        PhotoMetadata that = (PhotoMetadata) o;

        if (date != null ? !date.equals(that.date) : that.date != null)
            return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null)
            return false;
        if (photographer != null ?
                !photographer.equals(that.photographer) :
                that.photographer != null)
            return false;
        if (rating != that.rating)
            return false;
        if (!tags.equals(that.tags))
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + rating.hashCode();
        result = 31 * result + (photographer != null ? photographer.hashCode() : 0);
        result = 31 * result + tags.hashCode();
        return result;
    }

    @Override public String toString() {
        return "PhotoMetaData{" +
                "date=" + date +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", rating=" + rating +
                ", photographer='" + photographer + '\'' +
                ", tags=" + tags +
                '}';
    }
}
