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

    @Override
    public String toString() {
        return "Place{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
