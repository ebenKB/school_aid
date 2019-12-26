package com.hub.schoolAid;

import javax.persistence.*;

/**
 * Created by HUBKB.S on 11/20/2017.
 */
@Entity(name = "parent_address")
@Embeddable
public class Address {
    @Id
    @SequenceGenerator(name = "address_sequence", sequenceName = "address_sequence", allocationSize = 100)
    @GeneratedValue(generator = "address_sequence")
    private Long id;

    //other attributes
    private String homeAddress; // preferably digital address
    private String landmark;    // a description or closest landmark to the address
    private String location; // the exact place of the address
    private String email;

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
