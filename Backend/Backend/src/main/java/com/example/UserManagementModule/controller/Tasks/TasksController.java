package com.example.UserManagementModule.controller.Tasks;

import com.example.UserManagementModule.Exceptions.TaskAssignmentException;
import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.repository.Student.StudentRepository;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.service.Student.StudentService;
import com.example.UserManagementModule.service.Tasks.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tasks")
public class TasksController {

    private final TaskRepository taskRepository;
    private final StudentService studentService;
    private final TaskService taskService;

    public TasksController(TaskRepository taskRepository, StudentRepository studentRepository, StudentService studentService, TaskService taskService) {
        this.taskRepository = taskRepository;
        this.studentService = studentService;
        this.taskService = taskService;
    }

    @PostMapping("/add/{groupId}/{weekNum}")
    public ResponseEntity<Task> addTask(@RequestBody TaskRequest taskRequest, @PathVariable Integer weekNum, @PathVariable String groupId) {
        return new ResponseEntity<Task>(taskService.addTaskToWeek(weekNum, taskRequest, groupId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PostMapping("/{id}/{status}")
    public ResponseEntity<Task> changeTaskStatus(@PathVariable String id, @PathVariable String status) {
        return ResponseEntity.ok(taskService.changeTaskStatus(id, status));
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/{id}")
    public ResponseEntity<Task> assignAssigneeToTask(@PathVariable String id, @RequestBody List<String> assigneeIds) {
//        System.out.println(assigneeIds);
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        List<Student> assignees = task.getAssignee();

        assignees.forEach(assignee -> {
            if(assigneeIds.contains(assignee.getId())){
                throw new TaskAssignmentException("Assignee " + assignee.getUsername() + " already assigned to task.");
            }
        });

        assigneeIds.forEach(assigneeId -> {
            Student student = studentService.getStudentById(assigneeId).get();
            assignees.add(student);
        });

        task.setAssignee(assignees);

        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/assignees/{id}")
    public ResponseEntity<List<Student>> getAssigneeTasks(@PathVariable String id) {
        Task task = taskRepository.findById(id).get();
        return ResponseEntity.ok(task.getAssignee());
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<List<Comment>> getCommentsOfTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getCommentsOfTask(id));
    }

    @GetMapping("/count/{studentId}")
    public ResponseEntity<Long> getTaskCount(@PathVariable String studentId) {
        return ResponseEntity.ok(taskService.countTasksByStudentId(studentId));
    }

    @GetMapping("/count/{studentId}/{status}")
    public ResponseEntity<Long> getCountTasksByStudentIdAndStatus(@PathVariable String studentId, @PathVariable String status) {
        return ResponseEntity.ok(taskService.countTasksByStudentIdAndStatus(studentId, status));
    }

    @GetMapping("/s/{username}")
    public ResponseEntity<List<Task>> findCompletedTasksByStudent(@PathVariable String username) {
        return ResponseEntity.ok(taskService.findCompletedTasksByStudent(username));
    }

    @GetMapping("/s/all/{username}")
    public ResponseEntity<List<Task>> getTasksOfStudent(@PathVariable String username){
        return ResponseEntity.ok(taskService.findTasksByStudentId(username));
    }
}
