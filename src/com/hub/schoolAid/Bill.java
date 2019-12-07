package com.hub.schoolAid;

import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;
import java.time.LocalDate;
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

    public Bill() {
        this.isDeleted=false;
    }

//    @ManyToMany(cascade = CascadeType.ALL)
//    private List<Item>items;    // list of items contained in the bill e.g. sports dues, maintenance dues

    @ManyToMany(cascade = CascadeType.ALL)
    private List<BillItem> bill_items; // list of items contained in the bill

    private Double tuitionFee; // the total tuition fee for the bill
    private Double totalBill;  // the total cost of the bill
    private String academicYear; // the academic year for the bill
    private int createdBy; // the term value for the term that created the bill
    private int createdFor; // the term value for the term that the bill was created for
    private LocalDate createdAt; // the date the bill was created
    private Boolean isDeleted;

    @ManyToMany(cascade = { CascadeType.MERGE })
    private List<Student>students;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTutitionFee() {
        return tuitionFee;
    }

    public void setTutitionFee(Double tutitionFee) {
        this.tuitionFee = tutitionFee;
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

    public List<BillItem> getBill_items() {
        return bill_items;
    }

    public void setBill_items(List<BillItem> bill_items) {
        this.bill_items = bill_items;
    }

    public Double getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(Double tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
