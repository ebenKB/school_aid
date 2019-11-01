package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

@Entity
public class Guardian {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String fullname;
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
