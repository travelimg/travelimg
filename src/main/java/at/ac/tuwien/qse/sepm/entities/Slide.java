package at.ac.tuwien.qse.sepm.entities;

/**
 * Created by mb on 08.06.15.
 */
public class Slide {
    public Slide() {

    }

    private Integer id;
    private Integer photo_id;
    private Integer slideshow_id;
    private Integer order;

    public Slide(Integer id, Integer photo_id, Integer slideshow_id,Integer order){

        this.id = id;
        this.photo_id = photo_id;
        this.slideshow_id = slideshow_id;
        this.order = order;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(Integer photo_id) {
        this.photo_id = photo_id;
    }

    public Integer getSlideshow_id() {
        return slideshow_id;
    }

    public void setSlideshow_id(Integer slideshow_id) {
        this.slideshow_id = slideshow_id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
