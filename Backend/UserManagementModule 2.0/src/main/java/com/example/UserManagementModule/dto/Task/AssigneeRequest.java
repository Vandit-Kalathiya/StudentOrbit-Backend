package com.example.UserManagementModule.dto.Task;

import com.example.UserManagementModule.entity.Student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeRequest {
    private List<String> assigneeIds;

//    // Getters and setters
//    public List<String> getAssigneeIds() {
//        return assigneeIds;
//    }
//
//    public void setAssigneeIds(List<String> assigneeIds) {
//        this.assigneeIds = assigneeIds;
//    }
}

