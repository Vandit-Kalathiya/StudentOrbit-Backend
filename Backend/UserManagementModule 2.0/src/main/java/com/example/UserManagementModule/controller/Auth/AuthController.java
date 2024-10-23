package com.example.UserManagementModule.controller.Auth;

import com.example.UserManagementModule.dto.Jwt.JwtRequest;
import com.example.UserManagementModule.dto.Jwt.JwtResponse;
import com.example.UserManagementModule.service.Auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest)
    {
        return new ResponseEntity<>(authService.login(jwtRequest), HttpStatus.OK);
    }
}
