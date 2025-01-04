package com.example.UserManagementModule.service.Auth;

import com.example.UserManagementModule.dto.Jwt.JwtRequest;
import com.example.UserManagementModule.dto.Jwt.JwtResponse;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Session.Session;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Token.JwtToken;
import com.example.UserManagementModule.jwt.JwtAuthenticationHelper;
import com.example.UserManagementModule.repository.Session.SessionRepository;
import com.example.UserManagementModule.service.Token.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService {

    @Autowired
    AuthenticationManager manager;

    @Autowired
    JwtAuthenticationHelper jwtHelper;

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SessionRepository sessionRepository;

    public JwtResponse login(JwtRequest jwtRequest, HttpServletResponse response) {

        //authenticate with Authentication manager
        this.doAuthenticate(jwtRequest.getUsername(),jwtRequest.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername().toUpperCase());
//        System.out.println("userDetails: " + userDetails.getUsername());
        String token = jwtHelper.generateToken(userDetails);

        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setUsername(userDetails.getUsername());
        JwtToken savedToken = tokenService.saveToken(jwtToken);

        String sessionId = UUID.randomUUID().toString();
        this.saveSessionId(userDetails.getUsername(), sessionId);

        Cookie jwtCookie = new Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(false);
        jwtCookie.setSecure(false); // Only for HTTPS
        jwtCookie.setPath("/");    // Send this cookie on all paths
        jwtCookie.setMaxAge(7 * 24 * 60 * 60); // Expires in 1 day
        response.addCookie(jwtCookie);

//        System.out.println("token: " + savedToken.getToken());
        JwtResponse jwtResponse = JwtResponse.builder().jwtToken(token).role(userDetails.getUsername().length()>4?"student":"faculty").build();
        return jwtResponse;
    }

    private void doAuthenticate(String username, String password) {
        System.out.println("In authenticate...-------------------------------------------");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            manager.authenticate(authenticationToken);
        }catch (BadCredentialsException e) {
            System.out.println(e.getMessage()+"---------------------------------------");
            throw new BadCredentialsException("Invalid StudentID/FacultyID or Password");
        }
    }

    // Fetch current logged in user
    public ResponseEntity<?> getCurrentFaculty() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Faculty) {
            return ResponseEntity.ok((Faculty) principal);
        }else if(principal instanceof Student) {
            return ResponseEntity.ok((Student) principal);
        }

        throw new IllegalStateException("Authenticated principal is not a Faculty instance.");
    }

    public String getSessionIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean isValidSessionId(String sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findBySessionId(sessionId);

        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            // Ensure the session is not expired
            return LocalDateTime.now().isBefore(session.getExpiresAt());
        }

        return false; // Session ID does not exist or is expired
    }


    public void saveSessionId(String username, String sessionId) {
        // Save session ID to the database for the user
        Session session = new Session();
        session.setUsername(username);
        session.setSessionId(sessionId);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7)); // Example: 7-day expiration
        sessionRepository.save(session);
    }
}
