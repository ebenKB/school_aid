package com.hub.schoolAid;



import javax.persistence.*;

/**
 * Created by HUBKB.S on 11/30/2017.
 */
@Entity
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //other member variables
    private String fname;
    private String lname;
    private String oname;
    private String contact;

    //every staff has one login credentials
    @OneToOne
    private User userAccount;

    public Boolean registerNewStaff(Staff staff){
        return false;
    }

    public Boolean deleteStaff(Staff staff){
        return false;
    }

    public Boolean updateStaffDetails(Staff staff){
        return false;
    }

    public Boolean chageLoginCredentials(Staff  staff){
        return false;
    }
}
