package com.hub.schoolAid;

import javax.persistence.*;

/**
 * Created by HUBKB.S on 11/20/2017.
 */
@Entity(name = "parent_address")
@Embeddable
public class Address {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    //other attributes
    private String homeAddress;
    private String landmark;

    //getters and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
