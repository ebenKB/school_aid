package com.hub.schoolAid;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
public class StudentDetails {
    @Id
    private Long id;

    @OneToOne(cascade=CascadeType.ALL)
    @MapsId
    private Student student;

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
