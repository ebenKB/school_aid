package com.hub.schoolAid;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Contact {
    private String phone;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
