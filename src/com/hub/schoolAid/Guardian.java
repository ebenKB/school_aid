package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

/**
 * A guardian is any person whether related to the student or not who can stand in for the parents in their absence
 * In this context, we use the term Guardian to refer to any person who is mandated to pick up the child after school
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fullname", "contact"}, name = "guardian_constraint")}
)
public class Guardian {
    @javax.persistence.Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "guardian_sequence", sequenceName = "guardian_sequence", allocationSize = 100)
    @GeneratedValue(generator = "guardian_sequence")
    private Long Id;

    @Column
    private String fullname;

    @Column
    private String contact;

    @ManyToMany(mappedBy = "guardian")
    private List<Student> student;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
