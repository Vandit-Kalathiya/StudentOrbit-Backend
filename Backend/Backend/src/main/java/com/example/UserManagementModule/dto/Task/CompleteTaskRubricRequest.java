package com.example.UserManagementModule.dto.Task;

import lombok.Data;

@Data
public class CompleteTaskRubricRequest {
    private String name;
    private Integer score;
}
