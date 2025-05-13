package com.studentOrbit.generate_report_app.entity.Task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.studentOrbit.generate_report_app.entity.Comment.Comment;
import com.studentOrbit.generate_report_app.entity.Student.Student;
import com.studentOrbit.generate_report_app.entity.Weeks.Week;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @JsonIgnoreProperties("tasks")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "week_id")
    @ToString.Exclude
//    @JsonBackReference
    private Week week;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JsonManagedReference
    @JsonIgnore
    private List<Comment> comments;

    private LocalDate createdDate;

    private LocalDateTime submittedDate;

    private LocalDateTime completedDate;

    private LocalTime time;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Rubrics> rubrics = new ArrayList<>();

    private Integer scoredMarks = 0;

    public void addComment(Comment comment) {
        comment.setTask(this);
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTask(null);
    }
}
