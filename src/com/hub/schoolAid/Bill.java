package com.hub.schoolAid;

import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;
import java.util.List;

/**
 * This class represents all the expenses that students in a class or stage are supposed to pay
 * A bill is a collection of the tuition fee and all other expenses that will be allocated to a class
 */
@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    private List<Item>items;
    private Double tutitionFee;
    private Double totalBill;
    private String academicYear;
    private int createdBy;
    private int createdFor;

    @ManyToMany(mappedBy = "bills")
    private List<Student>students;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Double getTutitionFee() {
        return tutitionFee;
    }

    public void setTutitionFee(Double tutitionFee) {
        this.tutitionFee = tutitionFee;
    }

    public Double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(Double totalBill) {
        this.totalBill = totalBill;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getCreatedFor() {
        return createdFor;
    }

    public void setCreatedFor(int createdFor) {
        this.createdFor = createdFor;
    }
}
