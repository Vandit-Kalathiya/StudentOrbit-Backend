package com.example.UserManagementModule.service.Tasks;

import com.example.UserManagementModule.Helper.TaskStatus;
import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.repository.Week.WeekRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final FacultyGroupService facultyGroupService;
    private final WeekRepository weekRepository;
    private final TaskRepository taskRepository;

    public TaskService(FacultyGroupService facultyGroupService, WeekRepository weekRepository, TaskRepository taskRepository) {
        this.facultyGroupService = facultyGroupService;
        this.weekRepository = weekRepository;
        this.taskRepository = taskRepository;
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

    @Cacheable(value = "commentsOfTask",key = "#id")
    @CacheEvict(value = "commentsOfTask", allEntries = true)
    public List<Comment> getCommentsOfTask(String id){
        List<Comment> comments = taskRepository.findById(id).get().getComments();
        if(comments.isEmpty()){
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
        if(status.equals("IN_REVIEW")) {
            task.setSubmittedDate(LocalDateTime.now());
        }
        if(status.equals("COMPLETED")) {
            task.setCompletedDate(LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    public List<Task> findCompletedTasksByStudent(String username) {
        return taskRepository.findCompletedTasksByStudent(username);
    }
}
