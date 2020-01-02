package com.hub.schoolAid;

import javax.persistence.*;
import java.util.List;

//@Entity
public class Group {
    @Id
    @SequenceGenerator(name = "stage_sequence", sequenceName = "stage_sequence", allocationSize = 1)
    @GeneratedValue(generator = "stage_sequence")
    private Long id;
    private String name;

    @ManyToMany
    private List<Stage> stage;
}
