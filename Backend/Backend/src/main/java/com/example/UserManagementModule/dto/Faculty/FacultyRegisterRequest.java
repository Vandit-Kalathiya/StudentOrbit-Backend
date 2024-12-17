package com.example.UserManagementModule.dto.Faculty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacultyRegisterRequest {

    private String email;
    private String username;
    private String password;
    private String facultyName;
}
