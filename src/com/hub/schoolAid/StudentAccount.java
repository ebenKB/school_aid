package com.hub.schoolAid;

import javax.persistence.*;

@Entity(name = "StudentAccount")
public class StudentAccount {
    @javax.persistence.Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "student_account_sequence", sequenceName = "student_account_sequence", allocationSize = 1)
    @GeneratedValue(generator = "student_account_sequence")
    @Column(name = "account_id")
    private Long Id;
//    private double feeToPay = 0.0; // school fee credit
    private double schFeesPaid =0.0; // amount that has been paid for school fees
    private double feedingFeeToPay = 0.0;
    private double feedingFeeCredit = 0.0;
    private Double schoolFeesBalance;
    private Double schoolFeesDiscount = 0.0;
    private Double previousSchoolFeesBalance = 0.0;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

//    public double getFeeToPay() {
//        return feeToPay;
//    }
//
//    public void setFeeToPay(double feeToPay) {
//        this.feeToPay = feeToPay;
//    }

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

    public double getSchFeesPaid() {
        return schFeesPaid;
    }

    public void setSchFeesPaid(double schFeesPaid) {
        this.schFeesPaid = schFeesPaid;
    }

    public Double getSchoolFeesBalance() {
        return schoolFeesBalance;
    }

    public void setSchoolFeesBalance(Double schoolFeesBalance) {
        this.schoolFeesBalance = schoolFeesBalance;
    }

    public Double getSchoolFeesDiscount() {
        return schoolFeesDiscount;
    }

    public void setSchoolFeesDiscount(Double schoolFeesDiscount) {
        this.schoolFeesDiscount = schoolFeesDiscount;
    }

    public Double getPreviousSchoolFeesBalance() {
        return previousSchoolFeesBalance;
    }

    public void setPreviousSchoolFeesBalance(Double previousSchoolFeesBalance) {
        this.previousSchoolFeesBalance = previousSchoolFeesBalance;
    }
}
