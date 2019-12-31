package com.hub.schoolAid;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class TerminalReport {
    public TerminalReport(){
     this.setConduct("Satisfactory");
     this.setHeadTracherRemark("Satisfactory");
     this.setSelected(false);
    }
    
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "terminal_report_sequence", sequenceName = "terminal_report_sequence", allocationSize = 1)
    @GeneratedValue(generator = "terminal_report_sequence")
    private Long id;

    @OneToOne
    private Student student;

    private String conduct;

    private String headTracherRemark;

//    @Transient
//    private  Boolean isSelected;

    @Transient
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    @OneToOne
    private Stage promotedTo;

//    private int Attendance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getConduct() {
        return conduct;
    }

    public void setConduct(String conduct) {
        this.conduct = conduct;
    }

    public String getHeadTracherRemark() {
        return headTracherRemark;
    }

    public void setHeadTracherRemark(String headTracherRemark) {
        this.headTracherRemark = headTracherRemark;
    }

    public Stage getPromotedTo() {
        return promotedTo;
    }

    public void setPromotedTo(Stage promotedTo) {
        this.promotedTo = promotedTo;
    }

//    public int getAttendance() {
//        return Attendance;
//    }
//
//    public void setAttendance(int attendance) {
//        Attendance = attendance;
//    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
