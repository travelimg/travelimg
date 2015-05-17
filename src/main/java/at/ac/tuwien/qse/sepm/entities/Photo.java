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
