package at.ac.tuwien.qse.sepm.entities;

public class Place {

    private Integer id;
    private String city;
    private String country;
    private double latitude;
    private double longitude;

    private WikiPlaceInfo wikiPlaceInfo;

    public Place(Integer id, String city, String country, double latitude, double longitude) {
        this.id = id;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public WikiPlaceInfo getWikiPlaceInfo() { return wikiPlaceInfo; }

    public void setWikiPlaceInfo(WikiPlaceInfo wikiPlaceInfo) {
        this.wikiPlaceInfo = wikiPlaceInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Place place = (Place) o;

        if (!city.equals(place.city))
            return false;
        return country.equals(place.country);
    }

    @Override
    public int hashCode() {
        int result = city.hashCode();
        result = 31 * result + country.hashCode();
        return result;
    }

    @Override public String toString() {
        return "Place{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
