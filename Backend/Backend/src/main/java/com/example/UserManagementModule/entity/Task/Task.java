package com.example.UserManagementModule.entity.Task;

import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;
    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
           name = "task_assignee",
           joinColumns = @JoinColumn(name = "task_id"),
          inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> assignee;

    @ManyToOne()
    @JoinColumn(name = "week_id")
    @JsonBackReference
    private Week week;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
//    @JsonManagedReference
    @JsonIgnore
    private List<Comment> comments;

    private LocalDate createdDate;

    private LocalDateTime submittedDate;

    private LocalDateTime completedDate;

    private LocalTime time;

    public void addComment(Comment comment) {
        comment.setTask(this);
        this.comments.add(comment);
    }
}
