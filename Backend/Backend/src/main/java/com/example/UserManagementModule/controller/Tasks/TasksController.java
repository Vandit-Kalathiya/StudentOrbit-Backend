package com.example.UserManagementModule.controller.Tasks;

import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.repository.Student.StudentRepository;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.service.Student.StudentService;
import com.example.UserManagementModule.service.Tasks.TaskService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        Task task = taskRepository.findById(id).get();
        task.setStatus(status);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Task> assignAssigneeToTask(@PathVariable String id, @RequestBody List<String> assigneeIds) throws Exception {
//        System.out.println(assigneeIds);
        Task task = taskRepository.findById(id).orElseThrow(() -> new Exception("Task not found with id: " + id));

        List<Student> assignees = new ArrayList<>();

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
}
