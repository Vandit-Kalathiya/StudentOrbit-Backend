package com.example.UserManagementModule.controller.Group;

import com.example.UserManagementModule.Helper.TaskStatus;
import com.example.UserManagementModule.dto.Group.GroupRequest;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.service.Batch.BatchService;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/faculty/groups")
public class FacultyGroupController {

    @Autowired
    private FacultyGroupService groupService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private BatchService batchService;

    @GetMapping("/allGroups")
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/g/{name}")
    public ResponseEntity<Group> getGroupByName(@PathVariable String name) {
        Optional<Group> group = groupService.getGroupByName(name);
//        System.out.println("Data fetched...");
        return group.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable String id) {
        Optional<Group> group = groupService.getGroupById(id);
//        System.out.println("Data fetched...");
        return group.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestBody GroupRequest groupRequest) {
        if(groupService.findGroupByName(groupRequest.getGroupName()).isPresent()) {
            throw new RuntimeException("Group already exists with name : " + groupRequest.getGroupName());
        }
        Group group = new Group();
        group.setGroupName(groupRequest.getGroupName());
        group.setGroupDescription(groupRequest.getDescription());

        Set<String> technologies = new HashSet<>();
        groupRequest.getTechnologies().forEach(tech -> technologies.add(tech));

        group.setTechnologies(technologies);

        Set<Student> students = new HashSet<>();
        System.out.println(groupRequest.getStudents().isEmpty());
        groupRequest.getStudents().forEach(studentId -> {
            Student student = studentService.getStudentByUsername(studentId.toUpperCase()).get();
//            System.out.println(student.getUsername());
            students.add(student);
        });

        group.setStudents(students);

        group.setGroupLeader(groupRequest.getGroupLeaderId());
        group.setCreatedAt(LocalDateTime.now());

        System.out.println(groupRequest.getBatchName()+"========================");
        Batch batch = batchService.getBatchByBatchNameAndSemester(groupRequest.getBatchName().substring(1),Integer.parseInt(groupRequest.getBatchName().substring(0,1))).get();
        group.setBatchName(groupRequest.getBatchName());

        Set<Group> groups = batch.getGroups();
        groups.add(group);
        batch.setGroups(groups);

        List<Week> weeks = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            Week week = new Week();
            week.setWeekNumber(i);
            week.setTasks(new ArrayList<>());
            weeks.add(week);
            group.addWeek(week);
        }

        group.setWeeks(weeks);

        Group savedGroup = groupService.saveGroup(group);

//        savedGroup.getStudents().forEach(student -> {
//            List<Group> studentProjects = student.getProjects();
//            if(studentProjects == null) {
//                studentProjects = new ArrayList<>();
//            }
//            studentProjects.add(savedGroup);
//            student.setProjects(studentProjects);
//            studentService.saveStudent(student);
//        });

        return new ResponseEntity<>(savedGroup, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable String id, @RequestBody Group group) {
        if (groupService.getGroupById(id).isPresent()) {
            Group updatedGroup = groupService.getGroupById(id).get();

            updatedGroup.setGroupName(group.getGroupName());
            updatedGroup.setGroupDescription(group.getGroupDescription());
            updatedGroup.setTechnologies(group.getTechnologies());

            return new ResponseEntity<>(groupService.saveGroup(updatedGroup), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String id) {
        if (groupService.getGroupById(id).isPresent()) {
            groupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{name}")
    public Set<Group> getGroupByBatchName(@PathVariable String name) {
        System.out.println("name : " + name);
        return groupService.getGroupsByBatch(name);
    }
}
