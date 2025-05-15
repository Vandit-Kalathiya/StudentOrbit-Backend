package com.example.UserManagementModule.controller.Weeks;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import com.example.UserManagementModule.service.Weeks.WeekService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/week")
public class WeeksController {

    private static final Logger logger = LoggerFactory.getLogger(WeeksController.class);

    private final GroupRepository groupRepository;
    private final WeekService weekService;

    public WeeksController(GroupRepository groupRepository, WeekService weekService) {
        this.groupRepository = groupRepository;
        this.weekService = weekService;
    }

    @Cacheable(value = "weeksOfGroup", key = "#groupName")
    @GetMapping("/{groupName}")
    public ResponseEntity<?> findWeeksByGroupName(@PathVariable String groupName) {
        try {
            if (groupName == null || groupName.trim().isEmpty()) {
                logger.warn("Invalid group name: {}", groupName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group name cannot be null or empty");
            }

            Group group = groupRepository.findByGroupName(groupName)
                    .orElseThrow(() -> new EntityNotFoundException("Group not found with name: " + groupName));
            List<Week> weeks = group.getWeeks();
            if (weeks == null || weeks.isEmpty()) {
                logger.info("No weeks found for group: {}", groupName);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} weeks for group: {}", weeks.size(), groupName);
            return ResponseEntity.ok(weeks);
        } catch (EntityNotFoundException e) {
            logger.warn("Group not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Group not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving weeks for group {}: {}", groupName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve weeks: " + e.getMessage());
        }
    }

    @GetMapping("/{groupId}/{weekNum}")
    public ResponseEntity<?> getWeekByWeekNum(@PathVariable Integer weekNum, @PathVariable String groupId) {
        try {
            if (weekNum == null || weekNum <= 0) {
                logger.warn("Invalid week number: {}", weekNum);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Week number must be a positive integer");
            }
            if (groupId == null || groupId.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", groupId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Week week = weekService.getWeekByWeekNum(weekNum, groupId);
            if (week == null) {
                logger.warn("Week not found for group {} and week number {}", groupId, weekNum);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Week not found for group ID: " + groupId + " and week number: " + weekNum);
            }

            logger.info("Retrieved week {} for group: {}", weekNum, groupId);
            return ResponseEntity.ok(week);
        } catch (Exception e) {
            logger.error("Error retrieving week {} for group {}: {}", weekNum, groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve week: " + e.getMessage());
        }
    }

    @GetMapping("/w/{pName}/{weekNum}")
    public ResponseEntity<?> getWeekByProjectName(@PathVariable Integer weekNum, @PathVariable String pName) {
        try {
            if (weekNum == null || weekNum <= 0) {
                logger.warn("Invalid week number: {}", weekNum);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Week number must be a positive integer");
            }
            if (pName == null || pName.trim().isEmpty()) {
                logger.warn("Invalid project name: {}", pName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Project name cannot be null or empty");
            }

            Week week = weekService.getWeekByProjectName(weekNum, pName);
            if (week == null) {
                logger.warn("Week not found for project {} and week number {}", pName, weekNum);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Week not found for project: " + pName + " and week number: " + weekNum);
            }

            logger.info("Retrieved week {} for project: {}", weekNum, pName);
            return ResponseEntity.ok(week);
        } catch (Exception e) {
            logger.error("Error retrieving week {} for project {}: {}", weekNum, pName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve week: " + e.getMessage());
        }
    }
}