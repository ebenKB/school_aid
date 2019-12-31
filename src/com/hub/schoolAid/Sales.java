package com.hub.schoolAid;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by HUBKB.S on 11/20/2017.
 */
// a student can have a sale
@Entity
public class Sales {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "sales_sequence", sequenceName = "sales_sequence", allocationSize = 1)
    @GeneratedValue(generator = "sales_sequence")
    @Column(name = "sales_id")
    private Long id;

    //other attributes
   //@OneToOne(orphanRemoval = true,cascade = CascadeType.ALL)
//    @OneToOne(cascade = CascadeType.ALL)
//    private Item item;

    // CONSIDER REFACTORING  - A sale has many orders
    @OneToOne(cascade = CascadeType.ALL)
    private SaleOrder order;

    private Double amountPaid = 0.0;
    private Double totalcost  = 0.0;
    private Date date = new Date();

      @OneToOne
      @JoinColumn(name = "student_sale_id")
      private Student student;

   //getters and setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {

        this.amountPaid = amountPaid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Double getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(Double totalcost) {
        this.totalcost = totalcost;
    }

    public SaleOrder getOrder() {
        return order;
    }

    public void setOrder(SaleOrder order) {
        this.order = order;
    }

    //    public Item getItem() {
//        return item;
//    }
//
//    public void setItem(Item item) {
//        this.item = item;
//    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
//        String msg = this.getItem().getName() + " for "+ this.getStudent().toString();
        String msg = this.getOrder().getItem().getName() + "for "+ this.getStudent().toString();
        return msg;
    }
}
