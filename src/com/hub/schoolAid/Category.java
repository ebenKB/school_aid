package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

@Entity
public class Category {
    @Id
    @SequenceGenerator(name = "group_sequence", sequenceName = "group_sequence", allocationSize = 1)
    @GeneratedValue(generator = "group_sequence")
    private Long id;

    @ManyToMany()
    private List<Stage> stages;

    @OneToMany(mappedBy = "category")
    private List<Student>students;

    private String name;

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

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
