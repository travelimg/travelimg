package at.ac.tuwien.qse.sepm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by David on 19.05.2015.
 */
public class Journey {
    private int id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Journey(int id, String name, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Journey journey = (Journey) o;

        if (id != journey.id)
            return false;
        if (!name.equals(journey.name))
            return false;
        if (!startDate.equals(journey.startDate))
            return false;
        return endDate.equals(journey.endDate);

    }

    @Override public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }
}
