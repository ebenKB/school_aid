package com.hub.schoolAid;

import javax.persistence.*;

@Entity
public class PreviousSchool {
    @Id
    @SequenceGenerator(name = "picture_sequence", sequenceName = "picture_sequence", allocationSize = 100)
    @GeneratedValue(generator = "picture_sequence")
    private Long id;
    private String name;

    @OneToOne
    private Student student;

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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
