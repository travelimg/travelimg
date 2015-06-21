package at.ac.tuwien.qse.sepm.entities;

public class Slide {

    public Slide() {

    }

    private Integer id;
    private Integer slideshowId;
    private Integer order;
    private String caption;

    public Slide(Integer id, Integer slideshowId, Integer order, String caption) {
        this.id = id;
        this.slideshowId = slideshowId;
        this.order = order;
        this.caption = caption;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String toString() {
        return "Slide{" +
                "id=" + id +
                ", slideshowId=" + slideshowId +
                ", order=" + order +
                ", caption='" + caption + '\'' +
                '}';
    }
}
