package com.hub.schoolAid;

import javax.persistence.*;

@Entity
public class StudentConduct {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "student_conduct_sequence", sequenceName = "student_conduct_sequence", allocationSize = 100)
    @GeneratedValue(generator = "student_conduct_sequence")
    private Long id;

    private String conduct;

    public String getConduct() {
        return conduct;
    }

    public void setConduct(String conduct) {
        this.conduct = conduct;
    }
    @Override
    public String toString() {
        return this.getConduct();
    }
}
