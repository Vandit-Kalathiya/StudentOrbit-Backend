package com.example.UserManagementModule.entity.Comment;

import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Task.Task;
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
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Task task;

    private LocalDate date;

    private LocalTime time;
}
