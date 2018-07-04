package com.hub.schoolAid;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //pop up messages give the user some guides on start up
    private int canShowPopUp;
    private int canShowIntroHelp;

    public int getCanShowPopUp() {
        return canShowPopUp;
    }

    public void setCanShowPopUp(int canShowPopUp) {
        this.canShowPopUp = canShowPopUp;
    }
}
