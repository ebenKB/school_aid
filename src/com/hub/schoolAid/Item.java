package com.hub.schoolAid;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Embeddable
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double cost;
    private String name;
    private int qty;

//    @OneToMany
//    private List<Sales> sales =new ArrayList<>();

    //getters and setters
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
