package com.example.UserManagementModule.dto.Student;

import jakarta.persistence.Column;
import lombok.*;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResgisterRequest {

	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false, unique = true)
	private String username;
	@Column(nullable = false)
	private String password;
}
