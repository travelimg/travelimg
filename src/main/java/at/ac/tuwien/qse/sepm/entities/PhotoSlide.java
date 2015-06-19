package at.ac.tuwien.qse.sepm.entities;

public class PhotoSlide extends Slide {

    private Photo photo;

    public PhotoSlide(Integer id, Integer slideshowId, Integer order, String caption, Photo photo) {
        super(id, slideshowId, order, caption);

        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "PhotoSlide{" +
                "photo=" + photo +
                '}';
    }
}
