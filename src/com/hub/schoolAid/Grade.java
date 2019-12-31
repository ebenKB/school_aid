package com.hub.schoolAid;

import javax.persistence.*;

@Entity
public class Grade {

    public Grade(){

    }

    public Grade(String name){
        this.setName(name);
    }

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "grade_sequence", sequenceName = "grade_sequence", allocationSize = 1)
    @GeneratedValue(generator = "grade_sequence")
    private Long id;
    private String name;
    private String remark;
    private Double minMark;
    private Double maxMark;

    public Grade(String name, String remark) {
        this.setName(name);
        this.setRemark(remark);
    }

    public  Grade (String name,Double max, Double min,String remark){
        this.setName(name);
        this.setMaxMark(max);
        this.setMinMark(min);
        this.setRemark(remark);
    }

    //  getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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
