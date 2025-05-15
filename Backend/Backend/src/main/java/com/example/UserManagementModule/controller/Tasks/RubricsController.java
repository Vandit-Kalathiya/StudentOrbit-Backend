package com.example.UserManagementModule.controller.Tasks;

import com.example.UserManagementModule.dto.Task.RubricRequest;
import com.example.UserManagementModule.entity.Task.Rubrics;
import com.example.UserManagementModule.service.Tasks.RubricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rubrics")
public class RubricsController {

    private static final Logger logger = LoggerFactory.getLogger(RubricsController.class);

    private final RubricsService rubricsService;

    public RubricsController(RubricsService rubricsService) {
        this.rubricsService = rubricsService;
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<?> addRubric(@RequestBody RubricRequest rubricRequest, @PathVariable String taskId) {
        try {
            if (rubricRequest == null || rubricRequest.getRubricName() == null || rubricRequest.getRubricName().trim().isEmpty()) {
                logger.warn("Invalid rubric request: {}", rubricRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Rubric request cannot be null or have empty name");
            }
            if (taskId == null || taskId.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", taskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }

            Rubrics rubric = rubricsService.addRubrics(rubricRequest.getRubricName(), rubricRequest.getRubricScore(), taskId);
            logger.info("Rubric added successfully for task: {}", taskId);
            return new ResponseEntity<>(rubric, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for adding rubric: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding rubric for task {}: {}", taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add rubric: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRubricById(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid rubric ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Rubric ID cannot be null or empty");
            }

            Rubrics rubric = rubricsService.getRubricsById(id);
            if (rubric == null) {
                logger.warn("Rubric not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Rubric not found with ID: " + id);
            }

            logger.info("Retrieved rubric: {}", id);
            return ResponseEntity.ok(rubric);
        } catch (Exception e) {
            logger.error("Error retrieving rubric with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve rubric: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRubric(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid rubric ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Rubric ID cannot be null or empty");
            }

            rubricsService.deleteRubrics(id);
            logger.info("Rubric deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting rubric with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete rubric: " + e.getMessage());
        }
    }
}