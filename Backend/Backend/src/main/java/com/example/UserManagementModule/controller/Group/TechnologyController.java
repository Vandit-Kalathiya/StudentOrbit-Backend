package com.example.UserManagementModule.controller.Group;

import com.example.UserManagementModule.entity.Groups.Technology;
import com.example.UserManagementModule.service.Technology.TechnologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tech")
public class TechnologyController {

    private static final Logger logger = LoggerFactory.getLogger(TechnologyController.class);

    private final TechnologyService technologyService;

    public TechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    @PostMapping("/add/{groupId}")
    public ResponseEntity<?> addTechnology(@RequestBody String name, @PathVariable String groupId) {
        try {
            if (name == null || name.trim().isEmpty()) {
                logger.warn("Invalid technology name: {}", name);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Technology name cannot be null or empty");
            }
            if (groupId == null || groupId.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", groupId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Technology technology = technologyService.add(name.toLowerCase(), groupId);
            logger.info("Technology {} added to group: {}", name, groupId);
            return new ResponseEntity<>(technology, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for adding technology: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding technology {} to group {}: {}", name, groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add technology: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<?> deleteTechnology(@RequestBody List<String> technologiesToDelete, @PathVariable String groupId) {
        try {
            if (technologiesToDelete == null || technologiesToDelete.isEmpty()) {
                logger.warn("No technologies provided for deletion");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("At least one technology name is required");
            }
            if (groupId == null || groupId.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", groupId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            String result = technologyService.delete(technologiesToDelete, groupId);
            logger.info("Deleted technologies from group: {}", groupId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for deleting technologies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting technologies from group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete technologies: " + e.getMessage());
        }
    }
}