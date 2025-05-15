package com.example.UserManagementModule.controller.Batch;

import com.example.UserManagementModule.dto.Batch.BatchRequest;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import com.example.UserManagementModule.service.Batch.BatchService;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/faculty/batches")
public class FacultyBatchController {

    private static final Logger logger = LoggerFactory.getLogger(FacultyBatchController.class);

    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private FacultyService facultyService;

    @PostMapping("/add")
    public ResponseEntity<?> createBatch(@RequestBody BatchRequest batchRequest) {
        try {
            if (batchRequest == null || batchRequest.getBatchName() == null || batchRequest.getSemester() == null) {
                logger.warn("Invalid batch request: {}", batchRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Batch request cannot be null or missing required fields");
            }

            if (batchRepository.getBatchByBatchNameAndSemester(batchRequest.getBatchName(), batchRequest.getSemester()).isPresent()) {
                logger.warn("Batch already exists: {} for semester {}", batchRequest.getBatchName(), batchRequest.getSemester());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Batch already exists with batchName: " + batchRequest.getBatchName() + " and semester: " + batchRequest.getSemester());
            }

            Batch batch = new Batch();
            batch.setBatchName(batchRequest.getBatchName());
            batch.setSemester(batchRequest.getSemester());
            batch.setYear(batchRequest.getSemester() % 2 == 0 ? batchRequest.getSemester() / 2 : (batchRequest.getSemester() / 2) + 1);
            batch.setStartId(batchRequest.getStartId() != null ? batchRequest.getStartId().toUpperCase() : null);
            batch.setEndId(batchRequest.getEndId() != null ? batchRequest.getEndId().toUpperCase() : null);
            batch.setCreatedAt(LocalDateTime.now());

            Faculty faculty = facultyService.findFacultyByUserName(batchRequest.getAssignedFacultyUsername());
            if (faculty == null) {
                logger.warn("Faculty not found for username: {}", batchRequest.getAssignedFacultyUsername());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Faculty not found for username: " + batchRequest.getAssignedFacultyUsername());
            }
            batch.setAssignedFaculty(faculty);

            Batch savedBatch = batchService.saveBatch(batch);
            faculty.addBatch(savedBatch);
            facultyService.saveFaculty(faculty);

            logger.info("Batch created successfully: {}", savedBatch.getBatchName());
            return new ResponseEntity<>(savedBatch, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating batch: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create batch: " + e.getMessage());
        }
    }

    @GetMapping("/allBatches")
    public ResponseEntity<?> getAllBatches() {
        try {
            List<Batch> batches = batchService.getAllBatches();
            if (batches == null || batches.isEmpty()) {
                logger.info("No batches found");
                return ResponseEntity.ok(List.of());
            }
            logger.info("Retrieved {} batches", batches.size());
            return ResponseEntity.ok(batches);
        } catch (Exception e) {
            logger.error("Error retrieving all batches: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve batches: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBatchById(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid batch ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Batch ID cannot be null or empty");
            }

            Batch batch = batchService.getBatchById(id);
            if (batch == null) {
                logger.warn("Batch not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Batch not found with ID: " + id);
            }

            logger.info("Retrieved batch: {}", batch.getBatchName());
            return ResponseEntity.ok(batch);
        } catch (Exception e) {
            logger.error("Error retrieving batch with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve batch: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBatch(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid batch ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Batch ID cannot be null or empty");
            }

            Batch batch = batchService.getBatchById(id);
            if (batch == null) {
                logger.warn("Batch not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Batch not found with ID: " + id);
            }

            batchService.deleteBatch(id);
            logger.info("Batch deleted successfully: {}", id);
            return ResponseEntity.ok("Batch deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting batch with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete batch: " + e.getMessage());
        }
    }

    @GetMapping("/b/{username}")
    public ResponseEntity<?> getBatchesByUsername(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            Faculty faculty = facultyService.findFacultyByUserName(username);
            if (faculty == null) {
                logger.warn("Faculty not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Faculty not found for username: " + username);
            }

            List<Batch> batches = faculty.getBatches();
            if (batches == null || batches.isEmpty()) {
                logger.info("No batches found for username: {}", username);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} batches for username: {}", batches.size(), username);
            return ResponseEntity.ok(batches);
        } catch (Exception e) {
            logger.error("Error retrieving batches for username {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve batches: " + e.getMessage());
        }
    }

    @GetMapping("/g/{username}")
    public ResponseEntity<?> getGroupsByUsername(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            Faculty faculty = facultyService.findFacultyByUserName(username);
            if (faculty == null) {
                logger.warn("Faculty not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Faculty not found for username: " + username);
            }

            List<Group> groups = faculty.getGroups();
            if (groups == null || groups.isEmpty()) {
                logger.info("No groups found for username: {}", username);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} groups for username: {}", groups.size(), username);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving groups for username {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve groups: " + e.getMessage());
        }
    }

    @GetMapping("/allGroups/{sem}/{batchName}")
    public ResponseEntity<?> getAllGroupsOfBatchByBatchName(@PathVariable Integer sem, @PathVariable String batchName) {
        try {
            if (sem == null || sem <= 0) {
                logger.warn("Invalid semester: {}", sem);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Semester must be a positive integer");
            }
            if (batchName == null || batchName.trim().isEmpty()) {
                logger.warn("Invalid batch name: {}", batchName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Batch name cannot be null or empty");
            }

            List<Group> groups = batchService.getAllGroupsOfBatch(sem, batchName);
            if (groups == null || groups.isEmpty()) {
                logger.info("No groups found for batch: {} and semester: {}", batchName, sem);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} groups for batch: {} and semester: {}", groups.size(), batchName, sem);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving groups for batch {} and semester {}: {}", batchName, sem, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve groups: " + e.getMessage());
        }
    }

    @GetMapping("/{sem}/{batchName}")
    public ResponseEntity<?> getBatchesByBatchName(@PathVariable Integer sem, @PathVariable String batchName) {
        try {
            if (sem == null || sem <= 0) {
                logger.warn("Invalid semester: {}", sem);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Semester must be a positive integer");
            }
            if (batchName == null || batchName.trim().isEmpty()) {
                logger.warn("Invalid batch name: {}", batchName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Batch name cannot be null or empty");
            }

            Optional<Batch> batchOptional = batchService.getBatchByBatchNameAndSemester(batchName, sem);
            if (batchOptional.isEmpty()) {
                logger.warn("Batch not found for batchName: {} and semester: {}", batchName, sem);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Batch not found for batchName: " + batchName + " and semester: " + sem);
            }

            logger.info("Retrieved batch: {} for semester: {}", batchName, sem);
            return ResponseEntity.ok(batchOptional.get());
        } catch (Exception e) {
            logger.error("Error retrieving batch for batchName {} and semester {}: {}", batchName, sem, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve batch: " + e.getMessage());
        }
    }
}