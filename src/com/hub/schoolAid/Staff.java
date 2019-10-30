package com.hub.schoolAid;



import javax.persistence.*;
import java.util.List;

/**
 * Created by HUBKB.S on 11/30/2017.
 */
@Entity
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //other member variables
    private String full_name;
//    private String lname;
//    private String oname;
    private String contact;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    private List<Stage> classes;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Course>courses;

    //every staff has one login credentials
//    @OneToOne
//    private User userAccount;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = true)
    private Address address;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<Stage> getClasses() {
        return classes;
    }

    public void setClasses(List<Stage> classes) {
        this.classes = classes;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

//    public User getUserAccount() {
//        return userAccount;
//    }
//
//    public void setUserAccount(User userAccount) {
//        this.userAccount = userAccount;
//    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
