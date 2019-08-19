package com.hub.schoolAid;

import javax.persistence.*;

@Entity(name = "app")

public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //pop up messages give the user some guides on start up
    private Boolean canShowPopUp;
    private Boolean canShowIntroHelp;
    private Boolean hasInit;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Address address;

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
