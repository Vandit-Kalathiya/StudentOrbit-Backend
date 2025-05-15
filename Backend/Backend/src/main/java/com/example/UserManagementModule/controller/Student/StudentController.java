package com.example.UserManagementModule.controller.Student;

import com.example.UserManagementModule.dto.Student.ProfileUpdateRequest;
import com.example.UserManagementModule.dto.Student.StudentRequest;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Skills;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.repository.Skills.SkillsRepository;
import com.example.UserManagementModule.service.Student.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private SkillsRepository skillsRepository;

    @GetMapping("/allStudents")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            if (students == null || students.isEmpty()) {
                logger.info("No students found");
                return ResponseEntity.ok(List.of());
            }
            logger.info("Retrieved {} students", students.size());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error retrieving students: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve students: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }

            Optional<Student> student = studentService.getStudentById(id);
            if (student.isEmpty()) {
                logger.warn("Student not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }

            logger.info("Retrieved student: {}", id);
            return ResponseEntity.ok(student.get());
        } catch (Exception e) {
            logger.error("Error retrieving student with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve student: " + e.getMessage());
        }
    }

    @GetMapping("/u/{username}")
    public ResponseEntity<?> getStudentByUsername(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            Optional<Student> student = studentService.getStudentByUsername(username);
            if (student.isEmpty()) {
                logger.warn("Student not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with username: " + username);
            }

            logger.info("Retrieved student: {}", username);
            return ResponseEntity.ok(student.get());
        } catch (Exception e) {
            logger.error("Error retrieving student with username {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve student: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student student) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }
            if (student == null || student.getUsername() == null || student.getUsername().trim().isEmpty()) {
                logger.warn("Invalid student data: {}", student);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student data cannot be null or have empty username");
            }

            Optional<Student> existingStudent = studentService.getStudentById(id);
            if (existingStudent.isEmpty()) {
                logger.warn("Student not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }

            student.setId(id);
            Student updatedStudent = studentService.saveStudent(student);
            logger.info("Student updated successfully: {}", id);
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            logger.error("Error updating student with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update student: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }

            Optional<Student> student = studentService.getStudentById(id);
            if (student.isEmpty()) {
                logger.warn("Student not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }

            studentService.deleteStudent(id);
            logger.info("Student deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting student with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete student: " + e.getMessage());
        }
    }

    @GetMapping("/g/{SID}")
    public ResponseEntity<?> getStudentGroups(@PathVariable String SID) {
        try {
            if (SID == null || SID.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", SID);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }

            List<Group> groups = studentService.getStudentGroups(SID);
            if (groups == null || groups.isEmpty()) {
                logger.info("No groups found for student: {}", SID);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} groups for student: {}", groups.size(), SID);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving groups for student {}: {}", SID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve student groups: " + e.getMessage());
        }
    }

    @GetMapping("/gs/{SID}")
    public ResponseEntity<?> getStudentGroupsByProjectStatus(@PathVariable String SID) {
        try {
            if (SID == null || SID.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", SID);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }

            List<Group> groups = studentService.getStudentGroupsByProjectStatus(SID);
            if (groups == null || groups.isEmpty()) {
                logger.info("No groups found for student: {}", SID);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} groups for student: {}", groups.size(), SID);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving groups by project status for student {}: {}", SID, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve student groups: " + e.getMessage());
        }
    }

    @PostMapping("/skills/{id}")
    public ResponseEntity<?> addStudentSkill(@PathVariable String id, @RequestBody List<String> skills) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }
            if (skills == null || skills.isEmpty()) {
                logger.warn("No skills provided for student: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("At least one skill is required");
            }

            Student student = studentService.addStudentSkill(id, skills);
            if (student == null) {
                logger.warn("Student not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }

            logger.info("Added {} skills to student: {}", skills.size(), id);
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for adding skills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding skills to student {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add skills: " + e.getMessage());
        }
    }

    @GetMapping("/skills/{username}")
    public ResponseEntity<?> getStudentSkills(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            Set<Skills> skills = studentService.getStudentSkills(username);
            if (skills == null || skills.isEmpty()) {
                logger.info("No skills found for student: {}", username);
                return ResponseEntity.ok(Collections.emptyList());
            }

            logger.info("Retrieved {} skills for student: {}", skills.size(), username);
            return ResponseEntity.ok(skills.stream().toList());
        } catch (Exception e) {
            logger.error("Error retrieving skills for student {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve skills: " + e.getMessage());
        }
    }

    @DeleteMapping("/skills/{id}/{skill}")
    public ResponseEntity<?> deleteStudentSkill(@PathVariable String id, @PathVariable String skill) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }
            if (skill == null || skill.trim().isEmpty()) {
                logger.warn("Invalid skill: {}", skill);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Skill cannot be null or empty");
            }

            Set<Skills> skills = studentService.deleteStudentSkill(id, skill);
            if (skills == null) {
                logger.warn("Student not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }

            logger.info("Deleted skill {} from student: {}", skill, id);
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            logger.error("Error deleting skill {} from student {}: {}", skill, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete skill: " + e.getMessage());
        }
    }

    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateStudentProfile(@PathVariable String username, @RequestPart(required = false) ProfileUpdateRequest profileUpdateRequest, @RequestPart(required = false) MultipartFile image) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            Student student = studentService.updateStudentProfile(username, profileUpdateRequest, image);
            if (student == null) {
                logger.warn("Student not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with username: " + username);
            }

            logger.info("Updated profile for student: {}", username);
            return ResponseEntity.ok(student);
        } catch (IOException e) {
            logger.error("Error processing image for student {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to process image: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating profile for student {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update profile: " + e.getMessage());
        }
    }

    @GetMapping("/{username}/image")
    public ResponseEntity<?> getStudentImage(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            byte[] image = studentService.getProfileImage(username);
            if (image == null) {
                logger.warn("No image found for student: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No profile image found for student: " + username);
            }

            logger.info("Retrieved image for student: {}", username);
            return ResponseEntity.ok().body(image);
        } catch (Exception e) {
            logger.error("Error retrieving image for student {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve image: " + e.getMessage());
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody StudentRequest studentRequest) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }
            if (studentRequest == null || studentRequest.getUsername() == null || studentRequest.getUsername().trim().isEmpty()) {
                logger.warn("Invalid student request: {}", studentRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student request cannot be null or have empty username");
            }

            Student student = studentService.updateStudent(id, studentRequest);
            if (student == null) {
                logger.warn("Student not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }

            logger.info("Student updated successfully: {}", id);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            logger.error("Error updating student with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update student: " + e.getMessage());
        }
    }

    @GetMapping("/gitHubUrl/{username}")
    public ResponseEntity<?> getGitHubUrlFromUsername(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            Optional<Student> student = studentService.getStudentByUsername(username);
            if (student.isEmpty()) {
                logger.warn("Student not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No student found with username: " + username);
            }

            String gitHubUrl = student.get().getGitHubUrl();
            if (gitHubUrl == null || gitHubUrl.trim().isEmpty()) {
                logger.info("No GitHub URL found for student: {}", username);
                return ResponseEntity.ok("");
            }

            logger.info("Retrieved GitHub URL for student: {}", username);
            return ResponseEntity.ok(gitHubUrl);
        } catch (Exception e) {
            logger.error("Error retrieving GitHub URL for student {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve GitHub URL: " + e.getMessage());
        }
    }
}