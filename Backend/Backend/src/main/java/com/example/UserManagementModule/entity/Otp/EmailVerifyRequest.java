package com.example.UserManagementModule.entity.Otp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVerifyRequest {
    private String email;
    private String otp;
}