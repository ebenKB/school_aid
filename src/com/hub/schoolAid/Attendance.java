package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by HUBKB.S on 11/20/2017.
 */
@Entity
//@MappedSuperclass
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //other attributes
//  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

//  private int isPresent;
    private Double feedingFee;
    private LocalDate date;

    //getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
