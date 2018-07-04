package com.hub.schoolAid;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * this class represents the various terms that are in the acdemic year.
 * Every term the user has to set a new period
 */

@Entity
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private LocalDate start_date;
    private LocalDate end_date;
    private LocalDate today;
    /**
     *  0 means the term is
     *  1 means the term is current
     *  -1 means the term is completed
     */
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStart() {
        return start_date;
    }

    public void setStart(LocalDate start) {
        this.start_date = start;
    }

    public LocalDate getEnd() {
        return end_date;
    }

    public void setEnd(LocalDate end) {
        this.end_date = end;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDate getToday() {
        return today;
    }

    public void setToday(LocalDate today) {
        this.today = today;
    }
}
