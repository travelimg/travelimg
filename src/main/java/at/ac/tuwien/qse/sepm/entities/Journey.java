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
}
