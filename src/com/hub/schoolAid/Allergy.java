package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

@Entity
public class Allergy {
    @javax.persistence.Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "allergy_sequence", sequenceName = "allergy_sequence", allocationSize = 1)
    @GeneratedValue(generator = "allergy_sequence")
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
