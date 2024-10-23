package com.example.UserManagementModule.dto.Task;

import com.example.UserManagementModule.Helper.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest {
    private String taskName;
    private String taskDescription;
    private String taskStatus = String.valueOf(TaskStatus.TO_DO);
}
