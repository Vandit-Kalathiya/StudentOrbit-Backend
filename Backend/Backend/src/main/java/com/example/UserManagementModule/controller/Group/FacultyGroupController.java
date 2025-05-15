package com.example.UserManagementModule.controller.Group;

import com.example.UserManagementModule.dto.Group.GroupRequest;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Chat.Room;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Groups.Technology;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Technology.TechnologyRepository;
import com.example.UserManagementModule.service.Batch.BatchService;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import com.example.UserManagementModule.service.Student.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/faculty/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class FacultyGroupController {

    private static final Logger logger = LoggerFactory.getLogger(FacultyGroupController.class);

    @Autowired
    private FacultyGroupService groupService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/allGroups")
    public ResponseEntity<?> getAllGroups() {
        try {
            List<Group> groups = groupService.getAllGroups();
            if (groups == null || groups.isEmpty()) {
                logger.info("No groups found");
                return ResponseEntity.ok(List.of());
            }
            logger.info("Retrieved {} groups", groups.size());
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving groups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve groups: " + e.getMessage());
        }
    }

    @GetMapping("/g/{name}")
    public ResponseEntity<?> getGroupByName(@PathVariable String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                logger.warn("Invalid group name: {}", name);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group name cannot be null or empty");
            }

            Optional<Group> group = groupService.getGroupByName(name);
            if (group.isEmpty()) {
                logger.warn("Group not found for name: {}", name);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Group not found with name: " + name);
            }

            logger.info("Retrieved group: {}", name);
            return ResponseEntity.ok(group.get());
        } catch (Exception e) {
            logger.error("Error retrieving group with name {}: {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve group: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Optional<Group> group = groupService.getGroupById(id);
            if (group.isEmpty()) {
                logger.warn("Group not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Group not found with ID: " + id);
            }

            logger.info("Retrieved group: {}", id);
            return ResponseEntity.ok(group.get());
        } catch (Exception e) {
            logger.error("Error retrieving group with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve group: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest groupRequest) {
        try {
            if (groupRequest == null || groupRequest.getGroupName() == null || groupRequest.getGroupName().trim().isEmpty()) {
                logger.warn("Invalid group request: {}", groupRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group request cannot be null or have empty name");
            }

            if (groupService.getGroupByName(groupRequest.getGroupName()).isPresent()) {
                logger.warn("Group already exists with name: {}", groupRequest.getGroupName());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Group already exists with name: " + groupRequest.getGroupName());
            }

            Group group = new Group();
            group.setGroupName(groupRequest.getGroupName());
            group.setGroupDescription(groupRequest.getDescription());

            if (groupRequest.getBatchName() == null || !groupRequest.getBatchName().matches("\\d[A-Za-z0-9]+")) {
                logger.warn("Invalid batch name format: {}", groupRequest.getBatchName());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Batch name must start with a digit followed by letters or numbers");
            }

            int sem = Integer.parseInt(groupRequest.getBatchName().substring(0, 1));
            int year = sem % 2 == 0 ? sem / 2 : (sem / 2) + 1;

            if (groupRequest.getStudents() == null || groupRequest.getStudents().isEmpty()) {
                logger.warn("No students provided for group");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("At least one student is required");
            }

            String uniqueGroupId = groupService.generateUniqueID(groupRequest.getStudents().stream().findFirst().get(), year, sem, groupRequest.getBatchName().substring(1));
            group.setUniqueGroupId(uniqueGroupId);

            String url = "http://localhost:1821/api/v1/rooms/" + uniqueGroupId;
            ResponseEntity<Room> roomResponse;
            try {
                roomResponse = restTemplate.exchange(url, HttpMethod.POST, null, Room.class);
                if (!roomResponse.getStatusCode().is2xxSuccessful()) {
                    logger.warn("Failed to create room for group: {}", uniqueGroupId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to create chat room for group");
                }
            } catch (Exception e) {
                logger.error("Error creating chat room for group {}: {}", uniqueGroupId, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to create chat room: " + e.getMessage());
            }

            List<Technology> technologies = new ArrayList<>();
            if (groupRequest.getTechnologies() != null) {
                for (String tech : groupRequest.getTechnologies()) {
                    if (tech == null || tech.trim().isEmpty()) continue;
                    Optional<Technology> technology = technologyRepository.findByName(tech.toLowerCase());
                    if (technology.isPresent()) {
                        technologies.add(technology.get());
                    } else {
                        Technology newTechnology = Technology.builder().name(tech.toLowerCase()).build();
                        Technology savedTech = technologyRepository.save(newTechnology);
                        technologies.add(savedTech);
                    }
                }
            }
            group.setTechnologies(technologies);

            Set<Student> students = new HashSet<>();
            for (String studentId : groupRequest.getStudents()) {
                if (studentId == null || studentId.trim().isEmpty()) continue;
                Optional<Student> student = studentService.getStudentByUsername(studentId.toUpperCase());
                if (student.isEmpty()) {
                    logger.warn("Student not found: {}", studentId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Student not found with username: " + studentId);
                }
                students.add(student.get());
            }
            if (students.isEmpty()) {
                logger.warn("No valid students provided for group");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("At least one valid student is required");
            }
            group.setStudents(students);

            if (groupRequest.getStartDate() == null || groupRequest.getStartDate().trim().isEmpty()) {
                logger.warn("Invalid start date: {}", groupRequest.getStartDate());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Start date cannot be null or empty");
            }
            group.setStartDate(groupRequest.getStartDate());

            if (groupRequest.getGroupLeaderId() == null || groupRequest.getGroupLeaderId().trim().isEmpty()) {
                logger.warn("Invalid group leader ID: {}", groupRequest.getGroupLeaderId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group leader ID cannot be null or empty");
            }
            group.setGroupLeader(groupRequest.getGroupLeaderId().toUpperCase());
            group.setCreatedAt(LocalDateTime.now());

            Optional<Batch> batch = batchService.getBatchByBatchNameAndSemester(groupRequest.getBatchName().substring(1), sem);
            if (batch.isEmpty()) {
                logger.warn("Batch not found: {}", groupRequest.getBatchName());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Batch not found with name: " + groupRequest.getBatchName());
            }
            group.setBatchName(groupRequest.getBatchName());
            group.setBatch(batch.get());

            List<Week> weeks = new ArrayList<>();
            LocalDate currentDate;
            try {
                currentDate = LocalDate.parse(groupRequest.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                logger.warn("Invalid start date format: {}", groupRequest.getStartDate());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid start date format: " + e.getMessage());
            }

            for (int i = 1; i <= 12; i++) {
                Week week = new Week();
                week.setStartDate(currentDate);
                week.setEndDate(currentDate.plusDays(6));
                currentDate = currentDate.plusDays(7);
                week.setWeekNumber(i);
                week.setTasks(new ArrayList<>());
                weeks.add(week);
                group.addWeek(week);
            }
            group.setWeeks(weeks);

            Group savedGroup = groupService.saveGroup(group);
            batch.get().addGroup(savedGroup);
            batchService.saveBatch(batch.get());

            logger.info("Group created successfully: {}", savedGroup.getGroupName());
            return new ResponseEntity<>(savedGroup, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating group: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create group: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable String id, @RequestBody Group group) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }
            if (group == null || group.getGroupName() == null || group.getGroupName().trim().isEmpty()) {
                logger.warn("Invalid group data: {}", group);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group data cannot be null or have empty name");
            }

            Optional<Group> existingGroup = groupService.getGroupById(id);
            if (existingGroup.isEmpty()) {
                logger.warn("Group not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Group not found with ID: " + id);
            }

            Group updatedGroup = existingGroup.get();
            updatedGroup.setGroupName(group.getGroupName());
            updatedGroup.setGroupDescription(group.getGroupDescription());
            updatedGroup.setTechnologies(group.getTechnologies());

            Group savedGroup = groupService.saveGroup(updatedGroup);
            logger.info("Group updated successfully: {}", id);
            return ResponseEntity.ok(savedGroup);
        } catch (Exception e) {
            logger.error("Error updating group with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update group: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Optional<Group> group = groupService.getGroupById(id);
            if (group.isEmpty()) {
                logger.warn("Group not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Group not found with ID: " + id);
            }

            String result = groupService.deleteGroup(id);
            logger.info("Group deleted successfully: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error deleting group with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete group: " + e.getMessage());
        }
    }

    @GetMapping("/b/{name}")
    public ResponseEntity<?> getGroupByBatchName(@PathVariable String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                logger.warn("Invalid batch name: {}", name);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Batch name cannot be null or empty");
            }

            Set<Group> groups = groupService.getGroupsByBatch(name);
            if (groups == null || groups.isEmpty()) {
                logger.info("No groups found for batch: {}", name);
                return ResponseEntity.ok(Collections.emptySet());
            }

            logger.info("Retrieved {} groups for batch: {}", groups.size(), name);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving groups for batch {}: {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve groups: " + e.getMessage());
        }
    }

    @PutMapping("/complete/{id}")
    public ResponseEntity<?> markProjectCompleted(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Group group = groupService.markProjectCompleted(id);
            if (group == null) {
                logger.warn("Group not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Group not found with ID: " + id);
            }

            logger.info("Project marked as completed for group: {}", id);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            logger.error("Error marking project completed for group {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark project completed: " + e.getMessage());
        }
    }

    @PostMapping("/add/member/{id}")
    public ResponseEntity<?> addMember(@PathVariable String id, @RequestBody List<String> memberUsernames) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }
            if (memberUsernames == null || memberUsernames.isEmpty()) {
                logger.warn("No members provided for group: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("At least one member username is required");
            }

            Group group = groupService.addMember(id, memberUsernames);
            if (group == null) {
                logger.warn("Group not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Group not found with ID: " + id);
            }

            logger.info("Added {} members to group: {}", memberUsernames.size(), id);
            return ResponseEntity.ok(group);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for adding members: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding members to group {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add members: " + e.getMessage());
        }
    }

    @GetMapping("/week/{name}")
    public ResponseEntity<?> getWeekCount(@PathVariable String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                logger.warn("Invalid group name: {}", name);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group name cannot be null or empty");
            }

            int weekCount = groupService.getWeekCount(name);
            logger.info("Retrieved week count {} for group: {}", weekCount, name);
            return ResponseEntity.ok(weekCount);
        } catch (Exception e) {
            logger.error("Error retrieving week count for group {}: {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve week count: " + e.getMessage());
        }
    }

    @GetMapping("/gid/{gid}")
    public ResponseEntity<?> getGroupByGroupId(@PathVariable String gid) {
        try {
            if (gid == null || gid.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", gid);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Group group = groupService.getGroupByGroupId(gid);
            if (group == null) {
                logger.warn("Group not found for ID: {}", gid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Group not found with ID: " + gid);
            }

            logger.info("Retrieved group: {}", gid);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(group);
        } catch (Exception e) {
            logger.error("Error retrieving group with ID {}: {}", gid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve group: " + e.getMessage());
        }
    }

    @GetMapping("/members/{gid}")
    public ResponseEntity<?> getGroupMembers(@PathVariable String gid) {
        try {
            if (gid == null || gid.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", gid);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }

            Set<Student> members = groupService.getGroupMembers(gid);
            if (members == null || members.isEmpty()) {
                logger.info("No members found for group: {}", gid);
                return ResponseEntity.ok(Collections.emptySet());
            }

            logger.info("Retrieved {} members for group: {}", members.size(), gid);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            logger.error("Error retrieving members for group {}: {}", gid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve group members: " + e.getMessage());
        }
    }
}