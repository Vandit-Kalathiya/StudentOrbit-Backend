package com.example.UserManagementModule.controller.Auth;

import com.example.UserManagementModule.dto.Jwt.JwtRequest;
import com.example.UserManagementModule.dto.Jwt.JwtResponse;
import com.example.UserManagementModule.jwt.JwtAuthenticationHelper;
import com.example.UserManagementModule.service.Auth.AuthService;
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
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest)
    {
        return new ResponseEntity<>(authService.login(jwtRequest), HttpStatus.OK);
    }

//    @GetMapping("/current-user/{token}")
//    public String getCurrentUser(@PathVariable String token) {
//        return jwtAuthenticationHelper.getUsernameFromToken(token);
//    }

    @GetMapping("/current-user")
//    @CachePut(value = "currentUser", key = "#userDetails.username")
    public String getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "Logged-in user: " + userDetails.getUsername();
        } else {
            return "No authenticated user";
        }
    }
}
