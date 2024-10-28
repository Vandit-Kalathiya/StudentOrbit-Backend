package com.example.UserManagementModule.dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {

    private String commentDescription;
    private String facultyId;
    private String taskId;
}
