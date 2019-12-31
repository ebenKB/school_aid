package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

@Entity
public class Course {
    @javax.persistence.Id
//    @GeneratedValue(strategy = GenerationType.AUTO)

    @SequenceGenerator(name = "course_sequence", sequenceName = "course_sequence", allocationSize = 1)
    @GeneratedValue(generator = "course_sequence")
    @Column(name = "subject_id")
    private Long Id;

    private String name;

    @ManyToMany(mappedBy = "course")
    private List<Stage> stageList;

    @ManyToMany(mappedBy = "courses")
    private List<Staff>staffs;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
