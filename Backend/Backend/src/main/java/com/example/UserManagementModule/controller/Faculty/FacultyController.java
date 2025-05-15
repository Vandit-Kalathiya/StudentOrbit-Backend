package com.example.UserManagementModule.controller.Faculty;

import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculty")
@CrossOrigin(origins = "http://localhost:5173")
public class FacultyController {

    private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFaculty() {
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

    @PostMapping("/mentor/{facultyName}/{groupId}")
    public ResponseEntity<?> setMentor(@PathVariable String facultyName, @PathVariable String groupId) {
        try {
            if (facultyName == null || facultyName.trim().isEmpty()) {
                logger.warn("Invalid faculty name: {}", facultyName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Faculty name cannot be null or empty");
            }
            if (groupId == null || groupId.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", groupId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Group group = facultyService.selectMentor(facultyName, groupId);
            if (group == null) {
                logger.warn("Failed to assign mentor: Faculty {} or Group {} not found", facultyName, groupId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Faculty or group not found");
            }

            logger.info("Mentor {} assigned to group {}", facultyName, groupId);
            return ResponseEntity.ok(group);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for setting mentor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error setting mentor for faculty {} and group {}: {}", facultyName, groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to set mentor: " + e.getMessage());
        }
    }
}