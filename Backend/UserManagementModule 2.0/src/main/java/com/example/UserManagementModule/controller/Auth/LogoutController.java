package com.example.UserManagementModule.controller.Auth;

import com.example.UserManagementModule.entity.Token.JwtToken;
import com.example.UserManagementModule.jwt.JwtAuthenticationHelper;
import com.example.UserManagementModule.service.Auth.BlackListedTokenService;
import com.example.UserManagementModule.service.Token.TokenService;
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
    @Autowired
    private TokenService tokenService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println(tokenHeader);
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);

//            JwtToken jwtToken = tokenService.getToken(token);
            tokenService.deleteToken(token);

            System.out.println(token);
            Date expiryDate = jwtHelper.getExpirationDateFromToken(token);
            blacklistedTokenService.blacklistToken(token, expiryDate);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }
}
