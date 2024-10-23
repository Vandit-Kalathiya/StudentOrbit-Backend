package com.example.UserManagementModule.entity.Weeks;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Task.Task;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "weeks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private int weekNumber;

    @ManyToOne
    @JoinColumn(name = "student_group_id")
    @JsonBackReference // To avoid infinite recursion when serializing group
    private Group group;

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks;

    public void addTask(Task task) {
        task.setWeek(this);
        this.tasks.add(task);
    }
}
