package com.hub.schoolAid;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by HUBKB.S on 11/30/2017.
 */

@Entity
public class User implements Serializable{
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 100)
    @GeneratedValue(generator = "user_sequence")
    private Long id;

    //other field variables
    private String username;
    private String password;
    private String contact;
    private Boolean isactive;

    //the status tell whether the staff is admin or a normal user
    private  int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    /**
     *
     * @param password this function takes a password and return a hashed password
     * @return
     */
    public String hashPassword(String password)
    {
        return null;
    }

}
