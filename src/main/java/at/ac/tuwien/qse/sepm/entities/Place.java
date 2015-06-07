package at.ac.tuwien.qse.sepm.entities;

public class Place {

    private int id;
    private String city;
    private String country;

    public Place(int id, String city, String country) {
        this.id = id;
        this.city = city;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Place place = (Place) o;

        if (id != place.id)
            return false;
        if (!city.equals(place.city))
            return false;
        return country.equals(place.country);

    }

    @Override public int hashCode() {
        int result = id;
        result = 31 * result + city.hashCode();
        result = 31 * result + country.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Place{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
