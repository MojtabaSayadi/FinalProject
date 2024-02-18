package com.example.education.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "course_seq")
    @SequenceGenerator(name = "course_seq",sequenceName = "course_seq",allocationSize = 1)
    private long id;
    private String title;
    private  int code;

}
