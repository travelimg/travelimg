package at.ac.tuwien.qse.sepm.entities;

import java.util.Date;

public class Photo {

    private Integer id;
    private Photographer photographer;
    private String path;
    private Integer rating;
    private Exif exif;

    public Photo() {
    }

    public Photo(Integer id, Photographer photographer, String path, Integer rating) {
            this.id = id;
            this.photographer = photographer;
            this.path = path;
            this.rating = rating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Exif getExif() {
        return exif;
    }

    public void setExif(Exif exif) {
        this.exif = exif;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", photographer=" + photographer +
                ", path='" + path + '\'' +
                ", rating=" + rating +
                '}';
    }
}
