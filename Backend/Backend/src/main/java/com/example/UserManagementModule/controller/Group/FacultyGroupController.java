package com.example.UserManagementModule.controller.Group;

import com.example.UserManagementModule.dto.Group.GroupRequest;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Groups.Technology;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Technology.TechnologyRepository;
import com.example.UserManagementModule.service.Batch.BatchService;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/faculty/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class FacultyGroupController {

    @Autowired
    private FacultyGroupService groupService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private BatchService batchService;
    @Autowired
    private TechnologyRepository technologyRepository;

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
        if(groupService.getGroupByName(groupRequest.getGroupName()).isPresent()) {
            throw new RuntimeException("Group already exists with name : " + groupRequest.getGroupName());
        }
        Group group = new Group();
        group.setGroupName(groupRequest.getGroupName());
        group.setGroupDescription(groupRequest.getDescription());
        int sem = Integer.parseInt(groupRequest.getBatchName().substring(0,1));
        int year = sem%2==0?(Integer) (sem/2):(Integer) (sem/2)+1;
        group.setUniqueGroupId(groupService.generateUniqueID(groupRequest.getStudents().stream().toList().getFirst(),year,sem, groupRequest.getBatchName().substring(1)));

        List<Technology> technologies = new ArrayList<>();

        groupRequest.getTechnologies().forEach(tech -> {
            Optional<Technology> technology = technologyRepository.findByName(tech.toLowerCase());
            if(technology.isPresent()){
                technologies.add(technology.get());
            }else{
                Technology newTechnology = Technology.builder().name(tech.toLowerCase()).build();
                Technology newTech = technologyRepository.save(newTechnology);
                technologies.add(newTech);
            }
        });

        group.setTechnologies(technologies);

        Set<Student> students = new HashSet<>();
        groupRequest.getStudents().forEach(studentId -> {
            Student student = studentService.getStudentByUsername(studentId.toUpperCase()).orElseThrow();
            students.add(student);
        });

        group.setStudents(students);

        group.setStartDate(groupRequest.getStartDate());
        group.setGroupLeader(groupRequest.getGroupLeaderId().toUpperCase());
        group.setCreatedAt(LocalDateTime.now());

        Batch batch = batchService.getBatchByBatchNameAndSemester(groupRequest.getBatchName().substring(1),Integer.parseInt(groupRequest.getBatchName().substring(0,1))).get();
        group.setBatchName(groupRequest.getBatchName());

        List<Week> weeks = new ArrayList<>();

        LocalDate currentDate = LocalDate.parse(groupRequest.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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

        batch.addGroup(savedGroup);
        group.setBatch(batch);

        return new ResponseEntity<>(groupService.saveGroup(group), HttpStatus.CREATED);
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
    public ResponseEntity<String> deleteGroup(@PathVariable String id) {
        if (groupService.getGroupById(id).isPresent()) {
            return ResponseEntity.ok(groupService.deleteGroup(id));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/b/{name}")
    public Set<Group> getGroupByBatchName(@PathVariable String name) {
        System.out.println("name : " + name);
        return groupService.getGroupsByBatch(name);
    }

    @PutMapping("/complete/{id}")
    public ResponseEntity<Group> markProjectCompleted(@PathVariable String id) {
        return ResponseEntity.ok(groupService.markProjectCompleted(id));
    }

    @PostMapping("/add/member/{id}")
    public ResponseEntity<?> addMember(@PathVariable String id, @RequestBody List<String> memberUsername) {
        try{
            return ResponseEntity.ok(groupService.addMember(id,memberUsername));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/week/{name}")
    public ResponseEntity<?> getWeekCount(@PathVariable String name) {
        int weekCount = groupService.getWeekCount(name);
        return new ResponseEntity<>(weekCount, HttpStatus.OK);
    }

    @GetMapping("/gid/{gid}")
    public ResponseEntity<Group> getGroupByGroupId(@PathVariable String gid) {
        Group group = groupService.getGroupByGroupId(gid);
        System.out.println(group);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(group);
    }


    @GetMapping("/members/{gid}")
    public ResponseEntity<?> getGroupMembers(@PathVariable String gid) {
        return new ResponseEntity<>(groupService.getGroupMembers(gid), HttpStatus.OK);
    }
}
