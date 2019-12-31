package com.hub.schoolAid;

import com.hub.schoolAid.Item;

import javax.persistence.*;

@Entity
public class SaleOrder {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "sales_order_sequence", sequenceName = "sales_order_sequence", allocationSize = 1)
    @GeneratedValue(generator = "sales_order_sequence")
    private Long id;

    @OneToOne
    private Item item;

    private int quantity;

    @Column(precision = 2)
    private Double unitCost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }
}
