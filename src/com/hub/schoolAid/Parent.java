package com.hub.schoolAid;

import javax.persistence.*;

/**
 * Created by HUBKB.S on 11/18/2017.
 */

@Entity(name = "Parents")
@Embeddable
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
   // @Column(name = "id")
    private Long Id;

    /**
     * other properties of the parent
     */
    private String name;
    private String telephone;
    private String occupation;

    @OneToOne(orphanRemoval = true,cascade = CascadeType.ALL)
    private Address address;

    //getters and setters

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        this.Id = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
