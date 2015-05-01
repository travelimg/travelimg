package at.ac.tuwien.qse.sepm.entities;

public class Photographer {

    private Integer id;
    private String name;

    public Photographer() {
    }

    public Photographer(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Photographer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
