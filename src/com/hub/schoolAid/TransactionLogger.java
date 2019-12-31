package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class TransactionLogger {
    public TransactionLogger(){
        // set the default status to 1
        this.setStatus(1);
    }

    public TransactionLogger(Long transactionId_id,String description, String paidBy, LocalDate date, Double amount){
        this.setDescription(description);
        this.setPaidBy(paidBy);
        this.setAmount(amount);
        this.setDate(date);
        this.setTransactionId(transactionId_id);
    }

    @Id
    @SequenceGenerator(name = "transaction_logger_sequence", sequenceName = "transaction_logger_sequence", allocationSize = 1)
    @GeneratedValue(generator = "transaction_logger_sequence")
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double amount;
    private Double bal_before_payment;
    private Double bal_after_payment;
    private LocalDate date;
    private String Description;
    private String paidBy;
    private Long TransactionId; // refers to the unique id for the item that has been saved into the database.
    // the status of the transaction 1= NORMAL 0 = DELETED -1 = EDITED/UPDATED
    private int status;

    @OneToOne
    private Student student;

    @OneToOne(cascade = CascadeType.ALL)
    private Receipt receipt;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

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

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(Long transactionId) {
        TransactionId = transactionId;
    }

    public Double getBal_before_payment() {
        return bal_before_payment;
    }

    public void setBal_before_payment(Double bal_before_payment) {
        this.bal_before_payment = bal_before_payment;
    }

    public Double getBal_after_payment() {
        return bal_after_payment;
    }

    public void setBal_after_payment(Double bal_after_payment) {
        this.bal_after_payment = bal_after_payment;
    }

    @Override
    public String toString() {
        return this.getDescription()+ ","+ "with an amount of"+ this.getAmount() ;
    }
}
