package com.studentOrbit.generate_report_app.entity.Comment;

import com.studentOrbit.generate_report_app.entity.Faculty.Faculty;
import com.studentOrbit.generate_report_app.entity.Task.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "comment")
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
    @ToString.Exclude
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnore
    private Task task;

    private LocalDate date;

    private LocalTime time;

    @PreRemove
    private void preRemove() {
        if (faculty != null) {
            faculty.getComments().remove(this);
        }
        if (task != null) {
            task.getComments().remove(this);
        }
    }
}