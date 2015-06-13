package at.ac.tuwien.qse.sepm.entities;

public class Slide {

    public Slide() {

    }

    private Integer id;
    private Photo photo;
    private Integer slideshowId;
    private Integer order;

    public Slide(Integer id, Photo photo, Integer slideshowId, Integer order) {
        this.id = id;
        this.photo = photo;
        this.slideshowId = slideshowId;
        this.order = order;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Integer getSlideshowId() {
        return slideshowId;
    }

    public void setSlideshowId(Integer slideshowId) {
        this.slideshowId = slideshowId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
