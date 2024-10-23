package com.example.UserManagementModule.service.Tasks;

import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.repository.Week.WeekRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

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

    public Task addTaskToWeek(Integer weekNo, TaskRequest taskRequest, String groupId) {
        Group group = facultyGroupService.getGroupById(groupId).get();

        Week week = weekRepository.findByWeekNumberAndGroup(weekNo,group).get();
//        List<Task> tasks = week.getTasks();
        Task newTask = new Task();
        newTask.setName(taskRequest.getTaskName());
        newTask.setDescription(taskRequest.getTaskDescription());
        newTask.setStatus(taskRequest.getTaskStatus());
        newTask.setWeek(week);
        newTask.setDate(LocalDate.now());
        newTask.setTime(LocalTime.now());

//        List<Student> assignees = new ArrayList<>();
//        taskRequest.getTaskAssignes().forEach((assignee)->{
//            Student student = studentService.getStudentByUsername(assignee).get();
//            assignees.add(student);
//        });
//
//        newTask.setAssignee(assignees);
        Task savedTask = taskRepository.save(newTask);

//        tasks.add(newTask);
        week.addTask(savedTask);


        weekRepository.save(week);
        return savedTask;
    }
}
