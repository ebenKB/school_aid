package com.hub.schoolAid;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Embeddable
public class Item {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "item_sequence", sequenceName = "item_sequence", allocationSize = 1)
    @GeneratedValue(generator = "item_sequence")
    private Long id;

//    private Double cost;

//    private int qty;
    @Column(unique = true)
    private String name;

//    private String description;

    @OneToOne(mappedBy = "item")
    private SaleOrder order;

//    @OneToMany
//    private List<Sales> sales =new ArrayList<>();

    //getters and setters
//    public Double getCost() {
//        return cost;
//    }

//    public void setCost(Double cost) {
//        this.cost = cost;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // change all names to uppercase
        this.name = name.toUpperCase().trim();
    }

//    public int getQty() {
//        return qty;
//    }

//    public void setQty(int qty) {
//        this.qty = qty;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }

    @Override
    public String toString() {
        return this.name;
    }
}
