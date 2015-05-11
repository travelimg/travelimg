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
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        return (object.getClass() == this.getClass()
                && ((Tag) object).getId().equals(this.getId())
                && ((Tag) object).getName().equals(this.getName()));
    }
}
