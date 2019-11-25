package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

@Entity
public class Allergy {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String allergy;

    @ManyToMany
    private  List<Student> student;

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }
}
