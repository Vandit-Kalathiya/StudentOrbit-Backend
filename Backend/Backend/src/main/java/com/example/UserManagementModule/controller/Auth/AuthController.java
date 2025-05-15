package com.example.UserManagementModule.controller.Auth;

import com.example.UserManagementModule.dto.Jwt.JwtRequest;
import com.example.UserManagementModule.dto.Jwt.JwtResponse;
import com.example.UserManagementModule.jwt.JwtAuthenticationHelper;
import com.example.UserManagementModule.service.Auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    private JwtAuthenticationHelper jwtAuthenticationHelper;

    @PostMapping("/login")
//    @Cacheable(value = "jwtResponses", key = "#jwtRequest.username")
    public ResponseEntity<?> login(@RequestBody JwtRequest jwtRequest, HttpServletResponse response)
    {
        try{
            return new ResponseEntity<>(authService.login(jwtRequest, response), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/current-user")
//    @CachePut(value = "currentUser", key = "#userDetails.username")
    public ResponseEntity<?> getFacultyProfile() {
        Object currentFaculty = authService.getCurrentFaculty();
        return ResponseEntity.ok(currentFaculty);
    }
}
