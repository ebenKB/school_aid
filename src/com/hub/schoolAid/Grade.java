package com.hub.schoolAid;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Grade {

    public Grade(){

    }

    public Grade(char name){
        this.setName(name);
    }

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private char name;
    private String remark;
    private Double minMark;
    private Double maxMark;

    //getters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getMinMark() {
        return minMark;
    }

    public void setMinMark(Double minMark) {
        this.minMark = minMark;
    }

    public Double getMaxMark() {
        return maxMark;
    }

    public void setMaxMark(Double maxMark) {
        this.maxMark = maxMark;
    }
}
