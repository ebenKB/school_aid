package com.hub.schoolAid;

import javax.persistence.*;

@Entity
public class Picture {
    @Id
    @SequenceGenerator(name = "picture_sequence", sequenceName = "picture_sequence", allocationSize = 1)
    @GeneratedValue(generator = "picture_sequence")
    private Long id;

    @Lob
    private byte[] student_picture;

    @OneToOne
    private Student student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getStudent_picture() {
        return student_picture;
    }

    public void setStudent_picture(byte[] student_picture) {
        this.student_picture = student_picture;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
