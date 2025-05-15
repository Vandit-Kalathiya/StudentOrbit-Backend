package com.example.UserManagementModule.controller.Faculty;

import com.example.UserManagementModule.dto.Faculty.FacultyRegisterRequest;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/faculty")
public class FacultyRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(FacultyRegisterController.class);

    @Autowired
    private FacultyService facultyService;

    @GetMapping
    public ResponseEntity<?> getAllFaculties() {
        try {
            List<Faculty> faculties = facultyService.findAllFaculties();
            if (faculties == null || faculties.isEmpty()) {
                logger.info("No faculties found");
                return ResponseEntity.ok(List.of());
            }
            logger.info("Retrieved {} faculties", faculties.size());
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            logger.error("Error retrieving faculties: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve faculties: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerFaculty(@RequestBody FacultyRegisterRequest facultyRequest) {
        try {
            if (facultyRequest == null || facultyRequest.getUsername() == null || facultyRequest.getUsername().trim().isEmpty()) {
                logger.warn("Invalid faculty request: {}", facultyRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Faculty request cannot be null or have empty username");
            }

            facultyService.createFaculty(facultyRequest);
            logger.info("Faculty registered successfully: {}", facultyRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Faculty registered successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for registering faculty: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error registering faculty: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register faculty: " + e.getMessage());
        }
    }
}