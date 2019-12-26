package com.hub.schoolAid;

import javax.persistence.*;

@Entity
public class Remark {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "remark_sequence", sequenceName = "remark_sequence", allocationSize = 100)
    @GeneratedValue(generator = "remark_sequence")
    private Long id;

    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return this.remark;
    }
}

