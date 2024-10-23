package com.example.UserManagementModule.controller.Auth;

import com.example.UserManagementModule.jwt.JwtAuthenticationHelper;
import com.example.UserManagementModule.service.Auth.BlackListedTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class LogoutController {

    @Autowired
    private JwtAuthenticationHelper jwtHelper;

    @Autowired
    private BlackListedTokenService blacklistedTokenService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println(tokenHeader);
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            System.out.println(token);
            Date expiryDate = jwtHelper.getExpirationDateFromToken(token);
            blacklistedTokenService.blacklistToken(token, expiryDate);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }
}
