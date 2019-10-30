package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class AttendanceTemporary{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

 //   other attributes
//    @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "student_id", nullable = false)
     @ManyToOne(fetch = FetchType.LAZY)
     private Student student;

    private Double feedingFee;
    private LocalDate date;
    boolean present;
    boolean paidNow = true;

    // add a transient property to attendance temporary
    @Transient
    private Boolean selected =false;


    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public Double getFeedingFee() {
        return feedingFee;
    }

    public void setFeedingFee(Double feedingFee) {
        this.feedingFee = feedingFee;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean hasPaidNow() {
        return paidNow;
    }

    public void setPaidNow(boolean paidNow) {
        this.paidNow = paidNow;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
