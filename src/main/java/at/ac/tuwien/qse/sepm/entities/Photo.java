package at.ac.tuwien.qse.sepm.entities;

import java.time.LocalDate;

public class Photo {

    private Integer id;
    private Photographer photographer;
    private String path;
    private Rating rating;
    private LocalDate date;
    private double latitude;
    private double longitude;

    public Photo() {
    }

    public Photo(Integer id, Photographer photographer, String path, Rating rating, LocalDate date, double latitude, double longitude) {
        this.id = id;
        this.photographer = photographer;
        this.path = path;
        this.rating = rating;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Photo(Photo other) {
        this.id = other.id;
        this.photographer = other.photographer;
        this.path = other.path;
        this.rating = other.rating;
        this.date = other.date;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Rating getRating() {
        return rating;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (Double.compare(photo.latitude, latitude) != 0) return false;
        if (Double.compare(photo.longitude, longitude) != 0) return false;
        if (id != null ? !id.equals(photo.id) : photo.id != null) return false;
        if (photographer != null ? !photographer.equals(photo.photographer) : photo.photographer != null) return false;
        if (path != null ? !path.equals(photo.path) : photo.path != null) return false;
        if (rating != null ? !rating.equals(photo.rating) : photo.rating != null) return false;
        return !(date != null ? !date.equals(photo.date) : photo.date != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (photographer != null ? photographer.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", photographer=" + photographer +
                ", path='" + path + '\'' +
                ", rating=" + rating +
                ", date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
