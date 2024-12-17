package com.example.UserManagementModule.dto.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {

    private byte[] profilePicture;
    private String github;
    private String linkedin;
}
