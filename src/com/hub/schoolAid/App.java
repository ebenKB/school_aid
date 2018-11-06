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
    private Boolean canShowPopUp;
    private Boolean canShowIntroHelp;
    private Boolean hasInit;

    public Boolean getCanShowPopUp() {
        return canShowPopUp;
    }

    public void setCanShowPopUp(Boolean canShowPopUp) {
        this.canShowPopUp = canShowPopUp;
    }

    public Boolean getCanShowIntroHelp() {
        return canShowIntroHelp;
    }

    public void setCanShowIntroHelp(Boolean canShowIntroHelp) {
        this.canShowIntroHelp = canShowIntroHelp;
    }

    public Boolean getHasInit() {
        return hasInit;
    }

    public void setHasInit(Boolean hasInit) {
        this.hasInit = hasInit;
    }
}
