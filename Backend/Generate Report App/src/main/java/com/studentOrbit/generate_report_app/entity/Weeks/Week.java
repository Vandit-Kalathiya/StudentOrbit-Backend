package com.studentOrbit.generate_report_app.entity.Weeks;

import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.studentOrbit.generate_report_app.entity.Task.Task;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "weeks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Week implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private int weekNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_group_id")
    @JsonBackReference
    private Group group;

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference
//    @JsonBackReference
    private List<Task> tasks;

    private LocalDate startDate;

    private LocalDate endDate;

    public void addTask(Task task) {
        task.setWeek(this);
        this.tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setWeek(null);
    }
}
