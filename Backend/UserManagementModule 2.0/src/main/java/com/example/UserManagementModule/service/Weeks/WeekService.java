package com.example.UserManagementModule.service.Weeks;

import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Student.StudentRepository;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.repository.Week.WeekRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeekService {

    private final WeekRepository weekRepository;
    private final FacultyGroupService facultyGroupService;
    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final TaskRepository taskRepository;

    public WeekService(WeekRepository weekRepository, FacultyGroupService facultyGroupService, StudentRepository studentRepository, StudentService studentService, TaskRepository taskRepository) {
        this.weekRepository = weekRepository;
        this.facultyGroupService = facultyGroupService;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
        this.taskRepository = taskRepository;
    }

    public Week getWeekByWeekNum(Integer weekNum, String groupId) {
        Group group = facultyGroupService.getGroupById(groupId).get();
        Week week = weekRepository.findByWeekNumberAndGroup(weekNum,group).get();
        return week;
    }

    public Week getWeekByProjectName(Integer weekNum, String pName) {
        Group group = facultyGroupService.getGroupByName(pName).get();
        Week week = weekRepository.findByWeekNumberAndGroup(weekNum,group).get();
        return week;
    }
}
