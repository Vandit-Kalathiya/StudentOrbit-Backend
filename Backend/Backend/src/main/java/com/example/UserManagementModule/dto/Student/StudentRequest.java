package com.example.UserManagementModule.dto.Student;

import com.example.UserManagementModule.entity.Providers;
import com.example.UserManagementModule.entity.Student.Skills;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private boolean emailVerified;
    private Providers providers;
    private String gitHubUrl;
    private String linkedInUrl;
    private List<String> skills;
}
