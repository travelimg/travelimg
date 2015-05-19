package at.ac.tuwien.qse.sepm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by David on 19.05.2015.
 */
public class Journey {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String name;

    public Journey(LocalDateTime startDate, LocalDateTime endDate, String name) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
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
