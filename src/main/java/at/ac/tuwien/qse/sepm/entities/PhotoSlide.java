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
                "} " + super.toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PhotoSlide that = (PhotoSlide) o;

        return !(photo != null ? !photo.equals(that.photo) : that.photo != null);

    }

    @Override public int hashCode() {
        return photo != null ? photo.hashCode() : 0;
    }
}
