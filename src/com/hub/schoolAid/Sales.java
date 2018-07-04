package com.hub.schoolAid;



import javax.persistence.*;

/**
 * Created by HUBKB.S on 11/20/2017.
 */
// a student can have a sale
@Entity
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sales_id")
    private Long id;

    //other attributes
   //@OneToOne(orphanRemoval = true,cascade = CascadeType.ALL)
    @OneToOne(cascade = CascadeType.ALL)
    private Item item;

    private Double amountPaid = 0.0;
    private Double totalcost  = 0.0;

    //every sales has a student and many students can have the same sale -- depracated
//    @ManyToMany (mappedBy = "sales",fetch = FetchType.LAZY)
//    private List<Student> student = new ArrayList<Student>();

      @OneToOne
      @JoinColumn(name ="student_sale_id")
      private Student student;

//    //getters and setters
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

//    public int getQty() {
//        return qty;
//    }
//
//    public void setQty(int qty) {
//        this.qty = qty;
//    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    //methods

}
