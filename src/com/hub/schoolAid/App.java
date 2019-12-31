package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "app")

public class App {

    public App() {
        // set default app state
        this.setRegistered(false);
        this.setLastOpened(LocalDate.now());
    }

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "app_sequence", sequenceName = "app_sequence", allocationSize = 1)
    @GeneratedValue(generator = "app_sequence")
    private Long id;

    //pop up messages give the user some guides on start up
    private Boolean canShowPopUp;
    private Boolean canShowIntroHelp;
    private Boolean hasInit;
    private String contact;
    private String name;
    private String motto;
    private String address;
    private String currencyType = "GHC";
    private int maxCount;
    private int currentCount;
    private Boolean isRegistered;
    private LocalDate lastOpened;
    private String license = "";


    @Enumerated(EnumType.STRING)
    private FeedingType feedingType;

//    @OneToOne(cascade = CascadeType.PERSIST)
//    private Address address;

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

    public FeedingType getFeedingType() {
        return feedingType;
    }

    public void setFeedingType(FeedingType feedingType) {
        this.feedingType = feedingType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(LocalDate lastOpened) {
        this.lastOpened = lastOpened;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public Boolean getRegistered() {
        return isRegistered;
    }

    public void setRegistered(Boolean registered) {
        isRegistered = registered;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
