package com.studentOrbit.generate_report_app.Model;

import com.studentOrbit.generate_report_app.entity.Comment.Comment;
import com.studentOrbit.generate_report_app.entity.Task.Rubrics;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TaskData {
    private String taskId;
    private String taskName;
    private String description;
    private List<String> assignees;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private String status;
    private List<Comment> comments;
    private List<Rubrics> rubrics;

    public TaskData(String taskId, String taskName, String description, List<String> assignees,
                    LocalDate dueDate, LocalDate completionDate, String status) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.description = description;
        this.assignees = assignees;
        this.dueDate = dueDate;
        this.completionDate = completionDate;
        this.status = status;
        this.comments = List.of();
        this.rubrics = List.of();
    }
}
