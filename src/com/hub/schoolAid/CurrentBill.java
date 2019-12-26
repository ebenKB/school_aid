package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

@Entity
public class CurrentBill {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "current_bill_sequence", sequenceName = "current_bill_sequence", allocationSize = 100)
    @GeneratedValue(generator = "current_bill_sequence")
    private Long id;

    @OneToOne
    private Bill bill;

//    @OneToMany
//    private List<Stage> stages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

//    public List<Stage> getStages() {
//        return stages;
//    }
//
//    public void setStages(List<Stage> stages) {
//        this.stages = stages;
//    }
}
