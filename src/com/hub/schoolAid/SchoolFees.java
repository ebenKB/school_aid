package com.hub.schoolAid;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class SchoolFees {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Double amount;
    private String term;
    private LocalDate date;
}
