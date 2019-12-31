package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class SchoolFees {
    @javax.persistence.Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "school_fees_sequence", sequenceName = "school_fees_sequence", allocationSize = 1)
    @GeneratedValue(generator = "school_fees_sequence")
    private Long Id;

    private Double amount;
    private String term;
    private LocalDate date;
}
