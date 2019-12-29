package com.hub.schoolAid;

import javax.persistence.*;

@Entity
public class Picture {
    @Id
    @SequenceGenerator(name = "picture_sequence", sequenceName = "picture_sequence", allocationSize = 100)
    @GeneratedValue(generator = "picture_sequence")
    private Long id;

    @Lob
    private byte[] picture;

    @OneToOne
    private Student student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
