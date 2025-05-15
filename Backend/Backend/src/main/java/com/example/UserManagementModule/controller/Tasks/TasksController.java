package com.example.UserManagementModule.controller.Tasks;

import com.example.UserManagementModule.Exceptions.TaskAssignmentException;
import com.example.UserManagementModule.dto.Task.CompleteTaskRequest;
import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Task.Rubrics;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.service.Tasks.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tasks")
public class TasksController {

    private static final Logger logger = LoggerFactory.getLogger(TasksController.class);
    private final TaskService taskService;

    public TasksController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add/{groupId}/{weekNum}")
    public ResponseEntity<?> addTask(@RequestBody TaskRequest taskRequest, @PathVariable Integer weekNum, @PathVariable String groupId) {
        try {
            if (taskRequest == null || taskRequest.getTaskName() == null || taskRequest.getTaskName().trim().isEmpty()) {
                logger.warn("Invalid task request: {}", taskRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task request cannot be null or have empty name");
            }
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

            Task task = taskService.addTaskToWeek(weekNum, taskRequest, groupId);
            logger.info("Task added successfully to group {} week {}: {}", groupId, weekNum, task.getId());
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for adding task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding task to group {} week {}: {}", groupId, weekNum, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add task: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }

            Task task = taskService.getTask(id);
            if (task == null) {
                logger.warn("Task not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Task not found with ID: " + id);
            }

            logger.info("Retrieved task: {}", id);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            logger.error("Error retrieving task with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve task: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/{status}")
    public ResponseEntity<?> changeTaskStatus(@PathVariable String id, @PathVariable String status) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }
            if (status == null || status.trim().isEmpty()) {
                logger.warn("Invalid status: {}", status);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Status cannot be null or empty");
            }

            Task task = taskService.changeTaskStatus(id, status);
            logger.info("Task status changed to {} for task: {}", status, id);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for changing task status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error changing task status for task {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to change task status: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/c/{status}")
    public ResponseEntity<?> changeTaskStatusToComplete(@PathVariable String id, @PathVariable String status, @RequestBody CompleteTaskRequest completeTaskRequest) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }
            if (status == null || status.trim().isEmpty()) {
                logger.warn("Invalid status: {}", status);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Status cannot be null or empty");
            }
            if (completeTaskRequest == null) {
                logger.warn("Invalid complete task request: {}", completeTaskRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Complete task request cannot be null");
            }

            Task task = taskService.changeTaskStatusToCompleted(id, status, completeTaskRequest);
            logger.info("Task status changed to completed for task: {}", id);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for completing task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error completing task {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to complete task: " + e.getMessage());
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> assignAssigneeToTask(@PathVariable String id, @RequestBody List<String> assigneeIds) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }
            if (assigneeIds == null || assigneeIds.isEmpty()) {
                logger.warn("No assignees provided for task: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("At least one assignee ID is required");
            }

            Task task = taskService.assignAssigneesToTask(id, assigneeIds);
            logger.info("Assigned {} assignees to task: {}", assigneeIds.size(), id);
            return ResponseEntity.ok(task);
        } catch (TaskAssignmentException e) {
            logger.warn("Assignment error for task {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Assignment error: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            logger.warn("Resource not found for task {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Resource not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error assigning assignees to task {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to assign assignees: " + e.getMessage());
        }
    }

    @GetMapping("/assignees/{id}")
    public ResponseEntity<?> getAssigneeTasks(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }

            List<Student> assignees = taskService.getTask(id).getAssignee();
            if (assignees == null || assignees.isEmpty()) {
                logger.info("No assignees found for task: {}", id);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} assignees for task: {}", assignees.size(), id);
            return ResponseEntity.ok(assignees);
        } catch (Exception e) {
            logger.error("Error retrieving assignees for task {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve assignees: " + e.getMessage());
        }
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getCommentsOfTask(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }

            List<Comment> comments = taskService.getCommentsOfTask(id);
            if (comments == null || comments.isEmpty()) {
                logger.info("No comments found for task: {}", id);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} comments for task: {}", comments.size(), id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.error("Error retrieving comments for task {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve comments: " + e.getMessage());
        }
    }

    @GetMapping("/count/{studentId}")
    public ResponseEntity<?> getTaskCount(@PathVariable String studentId) {
        try {
            if (studentId == null || studentId.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", studentId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }

            Long count = taskService.countTasksByStudentId(studentId);
            logger.info("Retrieved task count {} for student: {}", count, studentId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error retrieving task count for student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve task count: " + e.getMessage());
        }
    }

    @GetMapping("/count/{studentId}/{status}")
    public ResponseEntity<?> getCountTasksByStudentIdAndStatus(@PathVariable String studentId, @PathVariable String status) {
        try {
            if (studentId == null || studentId.trim().isEmpty()) {
                logger.warn("Invalid student ID: {}", studentId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Student ID cannot be null or empty");
            }
            if (status == null || status.trim().isEmpty()) {
                logger.warn("Invalid status: {}", status);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Status cannot be null or empty");
            }

            Long count = taskService.countTasksByStudentIdAndStatus(studentId, status);
            logger.info("Retrieved task count {} for student {} with status {}: {}", count, studentId, status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error retrieving task count for student {} with status {}: {}", studentId, status, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve task count: " + e.getMessage());
        }
    }

    @GetMapping("/s/{username}")
    public ResponseEntity<?> findCompletedTasksByStudent(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            List<Task> tasks = taskService.findCompletedTasksByStudent(username);
            if (tasks == null || tasks.isEmpty()) {
                logger.info("No completed tasks found for student: {}", username);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} completed tasks for student: {}", tasks.size(), username);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving completed tasks for student {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve completed tasks: " + e.getMessage());
        }
    }

    @GetMapping("/s/all/{username}")
    public ResponseEntity<?> getTasksOfStudent(@PathVariable String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Invalid username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username cannot be null or empty");
            }

            List<Task> tasks = taskService.findTasksByStudentId(username);
            if (tasks == null || tasks.isEmpty()) {
                logger.info("No tasks found for student: {}", username);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} tasks for student: {}", tasks.size(), username);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving tasks for student {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve tasks: " + e.getMessage());
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String taskId, @RequestBody TaskRequest taskRequest) {
        try {
            if (taskId == null || taskId.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", taskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }
            if (taskRequest == null || taskRequest.getTaskName() == null || taskRequest.getTaskName().trim().isEmpty()) {
                logger.warn("Invalid task request: {}", taskRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task request cannot be null or have empty name");
            }

            Task task = taskService.updateTask(taskId, taskRequest);
            logger.info("Task updated successfully: {}", taskId);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for updating task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating task {}: {}", taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update task: " + e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}/{groupId}/{weekNo}")
    public ResponseEntity<?> deleteTask(@PathVariable String taskId, @PathVariable String groupId, @PathVariable Integer weekNo) {
        try {
            if (taskId == null || taskId.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", taskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }
            if (groupId == null || groupId.trim().isEmpty()) {
                logger.warn("Invalid group ID: {}", groupId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Group ID cannot be null or empty");
            }
            if (weekNo == null || weekNo <= 0) {
                logger.warn("Invalid week number: {}", weekNo);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Week number must be a positive integer");
            }

            String result = taskService.deleteTask(taskId, groupId, weekNo);
            logger.info("Task deleted successfully: {}", taskId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for deleting task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting task {}: {}", taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete task: " + e.getMessage());
        }
    }

    @GetMapping("/rubrics/{taskId}")
    public ResponseEntity<?> getRubricsOfTask(@PathVariable String taskId) {
        try {
            if (taskId == null || taskId.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", taskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }

            List<Rubrics> rubrics = taskService.getTask(taskId).getRubrics();
            if (rubrics == null || rubrics.isEmpty()) {
                logger.info("No rubrics found for task: {}", taskId);
                return ResponseEntity.ok(List.of());
            }

            logger.info("Retrieved {} rubrics for task: {}", rubrics.size(), taskId);
            return ResponseEntity.ok(rubrics);
        } catch (Exception e) {
            logger.error("Error retrieving rubrics for task {}: {}", taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve rubrics: " + e.getMessage());
        }
    }
}