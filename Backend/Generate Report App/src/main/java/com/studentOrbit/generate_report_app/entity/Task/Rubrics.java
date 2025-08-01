package com.studentOrbit.generate_report_app.entity.Task;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "rubrics")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rubrics implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String rubricName;
    private Integer rubricScore;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "task_id")
    @ToString.Exclude
    private Task task;
}
