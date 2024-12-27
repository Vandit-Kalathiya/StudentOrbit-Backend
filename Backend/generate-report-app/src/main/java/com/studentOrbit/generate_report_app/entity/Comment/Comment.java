package com.studentOrbit.generate_report_app.entity.Comment;

import com.studentOrbit.generate_report_app.entity.Faculty.Faculty;
import com.studentOrbit.generate_report_app.entity.Task.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

//@Entity
//@Table(name = "comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String commentDescription;

    @ManyToOne(fetch = FetchType.EAGER)
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    private Task task;

    private LocalDate date;

    private LocalTime time;
}
