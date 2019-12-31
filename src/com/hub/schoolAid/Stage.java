package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

/**
 * Created by HUBKB.S on 11/20/2017.
 */
@Entity(name="Class")
public class Stage {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "staff_sequence", sequenceName = "staff_sequence", allocationSize = 1)
    @GeneratedValue(generator = "staff_sequence")
    private Long id;

    //other fields variables
    private String name;
    private int classValue;
    private int num_on_roll;
    private Double feeding_fee;

    private Double feesToPay;  // REMOVE - DELETE

    @ManyToMany
    private List<Course> course;

    @ManyToMany(mappedBy = "classes", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Staff> staffs;

    // every stage has a current bill
//    @ManyToOne(optional = true)
//    private CurrentBill currentBill;

    @ManyToOne(optional = true)
    private Bill bill;

    //constructor
    public Stage () {

    }

    public Stage (Stage stage){
        this.setName(stage.getName());
        this.setNum_on_roll(stage.getNum_on_roll());
    }

    @OneToMany(mappedBy = "stage")
    private List<Student> students;

    @PreRemove
    public void checkReviewAssociationBeforeRemoval() {
        if (!this.students.isEmpty()) {
            throw new RuntimeException("Can't remove a stage that has students.");
        }
    }

    //getters and setters

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

    public int getNum_on_roll() {
        return num_on_roll;
    }

    public void setNum_on_roll(int num_on_roll) {
        this.num_on_roll = num_on_roll;
    }

    public int getClassValue() {
        return classValue;
    }

    public void setClassValue(int classValue) {
        this.classValue = classValue;
    }

    public Double getFeeding_fee() {
        return feeding_fee;
    }

    public void setFeeding_fee(Double feeding_fee) {
        this.feeding_fee = feeding_fee;
    }



    // DELETE
    public Double getFeesToPay() {
        return feesToPay;
    }

    // DELETE
    public void setFeesToPay(Double feesToPay) {
        this.feesToPay = feesToPay;
    }



    public List<Course> getCourse() {
        return course;
    }

    public void setCourse(List<Course> course) {
        this.course = course;
    }

    public Boolean isGreater(Stage stage1, Stage stage2){
        return stage1.getClassValue() > stage2.getClassValue();
    }

    public Boolean isEqual(Stage stage1,Stage stage2){
        return (stage1.getClassValue() == stage2.getClassValue());
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Staff> staffs) {
        this.staffs = staffs;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    //    public CurrentBill getCurrentBill() {
//        return currentBill;
//    }
//
//    public void setCurrentBill(CurrentBill currentBill) {
//        this.currentBill = currentBill;
//    }

    @Override
    public String toString() {
        return this.getName();
    }
}
