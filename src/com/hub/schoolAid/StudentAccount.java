package com.hub.schoolAid;

import javax.persistence.*;

@Entity(name = "StudentAccount")
public class StudentAccount {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    private Long Id;
    private double feeToPay=0.0;
    private double feedingFeeToPay = 0.0;
    private double feedingFeeCredit = 0.0;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public double getFeeToPay() {
        return feeToPay;
    }

    public void setFeeToPay(double feeToPay) {
        this.feeToPay = feeToPay;
    }

    public double getFeedingFeeToPay() {
        return feedingFeeToPay;
    }

    public void setFeedingFeeToPay(double feedingFeeToPay) {
        this.feedingFeeToPay = feedingFeeToPay;
    }

    public double getFeedingFeeCredit() {
        return feedingFeeCredit;
    }

    public void setFeedingFeeCredit(double feedingFeeCredit) {
        this.feedingFeeCredit = feedingFeeCredit;
    }
}
