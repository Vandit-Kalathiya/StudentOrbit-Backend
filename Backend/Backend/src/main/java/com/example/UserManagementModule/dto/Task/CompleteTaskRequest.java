package com.example.UserManagementModule.dto.Task;

import lombok.Data;

import java.util.List;

@Data
public class CompleteTaskRequest {
    private String taskId;
    private Integer maxScore;
    private Integer totalScore;
    private String remark;
    private List<CompleteTaskRubricRequest> grades;
}
