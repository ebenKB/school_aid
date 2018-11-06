package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class TransactionLogger {
    public TransactionLogger(){

    }

    public TransactionLogger(Long student_id,String description, String paidBy, LocalDate date, Double amount){
        this.setDescription(description);
        this.setPaidBy(paidBy);
        this.setAmount(amount);
        this.setDate(date);
        this.setStudent_id(student_id);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double amount;
    private LocalDate date;
    private String Description;
    private String paidBy;
    private Long Student_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public Long getStudent_id() {
        return Student_id;
    }

    public void setStudent_id(Long student_id) {
        Student_id = student_id;
    }
}
