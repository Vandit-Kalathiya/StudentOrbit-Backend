package com.example.UserManagementModule.service.Tasks;

import com.example.UserManagementModule.Exceptions.TaskAssignmentException;
import com.example.UserManagementModule.dto.Task.CompleteTaskRequest;
import com.example.UserManagementModule.dto.Task.CompleteTaskRubricRequest;
import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Task.Rubrics;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Task.RubricsRepository;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.repository.Week.WeekRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import com.example.UserManagementModule.service.Student.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final FacultyGroupService facultyGroupService;
    private final WeekRepository weekRepository;
    private final TaskRepository taskRepository;
    private final RubricsRepository rubricsRepository;
    private final StudentService studentService;

    public TaskService(FacultyGroupService facultyGroupService, WeekRepository weekRepository, TaskRepository taskRepository, RubricsRepository rubricsRepository, StudentService studentService) {
        this.facultyGroupService = facultyGroupService;
        this.weekRepository = weekRepository;
        this.taskRepository = taskRepository;
        this.rubricsRepository = rubricsRepository;
        this.studentService = studentService;
    }

    @CachePut(value = "tasks", key = "#weekNo + '_' + #groupId")
    @CacheEvict(value = "weeks", key = "#weekNo + '_' + #groupId") // Optional: Evicting the cache for weeks if needed
    public Task addTaskToWeek(Integer weekNo, TaskRequest taskRequest, String groupId) {
        Group group = facultyGroupService.getGroupById(groupId).orElseThrow(() ->
                new RuntimeException("Group not found with ID: " + groupId));

        Week week = weekRepository.findByWeekNumberAndGroup(weekNo, group).orElseThrow(() ->
                new RuntimeException("Week not found for number " + weekNo + " in group " + groupId));

        Task newTask = new Task();
        newTask.setName(taskRequest.getTaskName());
        newTask.setDescription(taskRequest.getTaskDescription());
        newTask.setStatus(taskRequest.getTaskStatus());
        newTask.setWeek(week);
        newTask.setCreatedDate(LocalDate.now());
        newTask.setTime(LocalTime.now());

        Task savedTask = taskRepository.save(newTask);

        week.addTask(savedTask);
        weekRepository.save(week);

        return savedTask;
    }

    @Cacheable(value = "commentsOfTask", key = "#id")
    @CacheEvict(value = "commentsOfTask", allEntries = true)
    public List<Comment> getCommentsOfTask(String id) {
        List<Comment> comments = taskRepository.findById(id).get().getComments();
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }
        return comments;
    }

    public Task getTask(String id) {
        return taskRepository.findById(id).get();
    }

    public Long countTasksByStudentId(String studentId) {
        return taskRepository.countTasksByStudentId(studentId.toUpperCase());
    }

    public Long countTasksByStudentIdAndStatus(String studentId, String status) {
        return taskRepository.countTasksByStudentAndStatusInGroup(studentId, status);
    }

    public Task changeTaskStatus(String id, String status) {
        Task task = taskRepository.findById(id).get();
        System.out.println(status);
        task.setStatus(status);
        if (status.equals("IN_REVIEW")) {
            task.setSubmittedDate(LocalDateTime.now());
        }
        if (status.equals("COMPLETED")) {
            task.setCompletedDate(LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    public Task changeTaskStatusToCompleted(String id, String status, CompleteTaskRequest completeTaskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus(status);
        task.setCompletedDate(LocalDateTime.now());

        int scoredMarks = completeTaskRequest.getGrades().stream()
                .mapToInt(CompleteTaskRubricRequest::getScore)
                .sum();

        List<Rubrics> rubricsList = completeTaskRequest.getGrades().stream()
                .map(g -> {
                    Rubrics rubrics = new Rubrics();
                    rubrics.setRubricName(g.getName());
                    rubrics.setRubricScore(g.getScore());
                    rubrics.setTask(task);
                    return rubricsRepository.save(rubrics); // Save each rubric
                })
                .toList();

        task.getRubrics().addAll(rubricsList);
        task.setScoredMarks(scoredMarks);

        return taskRepository.save(task);
    }

    public List<Task> findCompletedTasksByStudent(String username) {
        return taskRepository.findCompletedTasksByStudent(username);
    }

    public List<Task> findTasksByStudentId(String username) {
        return taskRepository.getTasksByStudentId(username);
    }

    public Task updateTask(String id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id).get();
        task.setName(taskRequest.getTaskName());
        task.setDescription(taskRequest.getTaskDescription());
        task.setStatus(taskRequest.getTaskStatus());
        return taskRepository.save(task);
    }

    public Task assignAssigneesToTask(String taskId, List<String> assigneeIds) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
        List<Student> assignees = task.getAssignee();
        for (String assigneeId : assigneeIds) {
            if (assignees.stream().anyMatch(a -> a.getId().equals(assigneeId))) {
                throw new TaskAssignmentException("Assignee " + assigneeId + " already assigned to task.");
            }
            Student student = studentService.getStudentById(assigneeId)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + assigneeId));
            assignees.add(student);
        }
        task.setAssignee(assignees);
        return taskRepository.save(task);
    }

    public String deleteTask(String taskId, String groupId, Integer weekNo) {
        Group group = facultyGroupService.getGroupById(groupId).orElseThrow(() ->
                new RuntimeException("Group not found with ID: " + groupId));

        Week week = weekRepository.findByWeekNumberAndGroup(weekNo, group).orElseThrow(() ->
                new RuntimeException("Week not found for number " + weekNo + " in group " + group.getGroupName()));

        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new RuntimeException("Task not found with ID: " + taskId));

        week.removeTask(task);
        weekRepository.save(week);
        taskRepository.deleteById(taskId);
        return "Task deleted successfully";
    }
}
