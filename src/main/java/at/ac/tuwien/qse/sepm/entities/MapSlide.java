package at.ac.tuwien.qse.sepm.entities;


public class MapSlide extends Slide {

    private double latitude;
    private double longitude;
    private int zoomLevel;

    public MapSlide(Integer id, Integer slideshowId, Integer order, String caption, double latitude, double longitude, int zoomLevel) {
        super(id, slideshowId, order, caption);

        this.latitude = latitude;
        this.longitude = longitude;
        this.zoomLevel = zoomLevel;
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

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MapSlide mapSlide = (MapSlide) o;

        if (Double.compare(mapSlide.latitude, latitude) != 0)
            return false;
        if (Double.compare(mapSlide.longitude, longitude) != 0)
            return false;
        return zoomLevel == mapSlide.zoomLevel;

    }

    @Override public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + zoomLevel;
        return result;
    }

    @Override public String toString() {
        return "MapSlide{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", zoomLevel=" + zoomLevel +
                "} " + super.toString();
    }
}
