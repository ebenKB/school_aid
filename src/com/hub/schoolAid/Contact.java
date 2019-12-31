package com.hub.schoolAid;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

public class Contact {
    private String phone;

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "contact_sequence", sequenceName = "contact_sequence", allocationSize = 1)
    @GeneratedValue(generator = "contact_sequence")
    private Long id;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
