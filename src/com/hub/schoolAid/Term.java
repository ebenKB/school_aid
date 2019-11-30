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

    private String name; // name is the name given to the term e.g first term or second term
    private int value; // the value of the term. This value is an integer
    private LocalDate start_date; // the date that the term started
    private LocalDate end_date; // the date that the term ended
    private LocalDate today; // the current day of the term

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
