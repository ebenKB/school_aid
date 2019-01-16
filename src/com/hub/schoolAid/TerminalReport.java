package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class TerminalReport {
    public TerminalReport(){
     this.setConduct("Satisfactory");
     this.setHeadTracherRemark("Satisfactory");
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Student student;

    private String conduct;

    private String headTracherRemark;

    @OneToOne
    private Stage promotedTo;

    private int Attendance;

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

    public int getAttendance() {
        return Attendance;
    }

    public void setAttendance(int attendance) {
        Attendance = attendance;
    }
}
