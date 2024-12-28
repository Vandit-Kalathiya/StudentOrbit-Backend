package com.example.UserManagementModule.service.Group;

import com.example.UserManagementModule.Helper.TaskStatus;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Groups.UniqueGroupId;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import com.example.UserManagementModule.repository.Group.UniqueGroupIdRepository;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FacultyGroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private UniqueGroupIdRepository uniqueGroupIdRepository;

    //    @Cacheable(value = "allGroups")
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

//    @Cacheable(value = "groupById", key = "#id")
    public Optional<Group> getGroupById(String id) {
        return groupRepository.findById(id);
    }

//    @CacheEvict(value = "allGroups", allEntries = true)
    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

//    @CacheEvict(value = "allGroups", allEntries = true)
    public void deleteGroup(String id) {
        groupRepository.deleteById(id);
    }

//    @Cacheable(value = "groupByName", key = "#name")
    public Optional<Group> getGroupByName(String name) {
        return groupRepository.findByGroupName(name);
    }

//    @Cacheable(value = "groupsByBatch", key = "#name")
    public Set<Group> getGroupsByBatch(String name) {
        return groupRepository.findGroupsByBatchName(name);
    }

    public Group markProjectCompleted(String id) {
        Group group = groupRepository.findById(id).get();
        group.setProjectStatus(TaskStatus.COMPLETED.name());
        return this.saveGroup(group);
    }

    public Group addMember(String id, List<String> memberUsername) {
        Group group = groupRepository.findById(id).get();

        memberUsername.forEach(member -> {
            Optional<Student> studentOptional = studentService.getStudentByUsername(member.toUpperCase());
            if(studentOptional.isPresent()) {
                Student student = studentOptional.get();
                group.getStudents().forEach(student1 -> {
                    if(student1.getUsername().equalsIgnoreCase(student.getUsername())) {
                        throw new RuntimeException("Student is already member of " + group.getGroupName() + ".");
                    }
                });
                Set<Student> students = group.getStudents();
                students.add(student);
                group.setStudents(students);
            }else {
                throw new RuntimeException("Student "+ member +" not found.");
            }
        });

        return this.saveGroup(group);
    }

    public String generateUniqueID(String dept, int graduationYear, int semester, String batch) {
        int currentYear = java.time.Year.now().getValue();

        dept = dept.substring(2,4).toUpperCase();

        String uniqueID;
        Random random = new Random();

        List<UniqueGroupId> uniqueIDs = uniqueGroupIdRepository.findAll();
        List<String> allIds = new ArrayList<>();

        uniqueIDs.forEach(id -> allIds.add(id.getUniqueGroupId()));

        do {
            int randomTwoDigits = random.nextInt(100);

            String randomTwoDigitsStr = String.format("%02d", randomTwoDigits);

            uniqueID = dept + currentYear + graduationYear + semester + batch + randomTwoDigitsStr;

        } while (allIds.contains(uniqueID));

        uniqueGroupIdRepository.save(new UniqueGroupId(uniqueID));

        return uniqueID;
    }

    public int getWeekCount(String name) {
        Optional<Group> g = getGroupByName(name);
        System.out.println("Week size "+g.get().getWeeks().size());
        int count = 0;
        Group group = g.get();
        System.out.println("Group name " + group.getWeeks());
        List<Week> weeks = group.getWeeks();

        for (Week week : weeks) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(week.getStartDate().toString(), formatter);
            LocalDate endDate = LocalDate.parse(week.getEndDate().toString(), formatter);

            System.out.println("Week " + startDate + " - " + endDate);

            if (endDate.isBefore(LocalDate.now())) {
                count++;
            } else if (startDate.isBefore(LocalDate.now()) && endDate.isAfter(LocalDate.now())) {
                count++;
            }
        }

        return count;
    }
}


