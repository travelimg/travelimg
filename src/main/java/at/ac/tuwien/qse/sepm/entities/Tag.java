package at.ac.tuwien.qse.sepm.entities;

public class Tag {

    private Integer id;
    private String name;

    public Tag() {
    }

    public Tag(Integer id, String name) {
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
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return (o.getClass() == this.getClass()
                && ((Tag) o).getId().equals(this.getId())
                && ((Tag) o).getName().equals(this.getName()));
    }
}
