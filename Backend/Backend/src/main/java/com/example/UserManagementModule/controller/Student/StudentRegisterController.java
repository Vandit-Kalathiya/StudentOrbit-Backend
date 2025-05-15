package com.example.UserManagementModule.controller.Student;

import com.example.UserManagementModule.dto.Student.StudentResgisterRequest;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.service.Student.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/student")
public class StudentRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(StudentRegisterController.class);

    private final StudentService studentService;

    @Autowired
    public StudentRegisterController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody StudentResgisterRequest studentRequest) {
        try {
            if (studentRequest == null || studentRequest.getUsername() == null || studentRequest.getUsername().trim().isEmpty()) {
                logger.warn("Invalid student request: {}", studentRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student request cannot be null or have empty username");
            }

            studentService.createStudent(studentRequest);
            logger.info("Student registered successfully: {}", studentRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Student registered successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for registering student: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error registering student: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register student: " + e.getMessage());
        }
    }
}