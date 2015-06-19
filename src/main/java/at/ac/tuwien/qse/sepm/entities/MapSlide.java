package at.ac.tuwien.qse.sepm.entities;


public class MapSlide extends Slide {

    private double latitude;
    private double longitude;

    public MapSlide(Integer id, Integer slideshowId, Integer order, String caption, double latitude, double longitude) {
        super(id, slideshowId, order, caption);

        this.latitude = latitude;
        this.longitude = longitude;
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
        return "MapSlide{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
