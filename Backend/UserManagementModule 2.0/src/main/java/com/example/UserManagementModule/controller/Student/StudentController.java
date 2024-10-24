package com.example.UserManagementModule.controller.Student;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Skills;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.repository.Skills.SkillsRepository;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private SkillsRepository skillsRepository;

    @GetMapping("/allStudents")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student student) {
        if (studentService.getStudentById(id).isPresent()) {
            student.setId(id);
            Student updatedStudent = studentService.saveStudent(student);
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        if (studentService.getStudentById(id).isPresent()) {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/g/{SID}")
    public ResponseEntity<List<Group>> getStudentGroups(@PathVariable String SID) {
        return ResponseEntity.ok(studentService.getStudentGroups(SID));
    }

    @PostMapping("/skills/{id}")
    public ResponseEntity<Student> addStudentSkill(@PathVariable String id, @RequestBody List<String> skills) {
        if (!studentService.getStudentByUsername(id.toUpperCase()).isPresent()) {
            throw new RuntimeException("Student not found for id : "+id.toUpperCase());
        }
        Student student = studentService.getStudentByUsername(id.toUpperCase()).get();

        Set<Skills> skillsList = student.getSkills();
        Set<Skills> tempList = student.getSkills();
        if(skillsList == null) {
            skillsList = new HashSet<>();
        }
        skills.forEach(skill -> {
            if(skillsRepository.findByName(skill) == null) {
                Skills newSkill = new Skills(skill);
                Skills savedSkill = skillsRepository.save(newSkill);
                tempList.add(savedSkill);
            }
            else{
                Skills getSavedSkill = skillsRepository.findByName(skill);
                tempList.add(getSavedSkill);
            }
        });
        if(tempList != null) {
            skillsList.addAll(tempList);
        }
        student.setSkills(skillsList);

        return new ResponseEntity<>(studentService.saveStudent(student), HttpStatus.OK);
    }

    @GetMapping("/skills/{username}")
    public ResponseEntity<Set<Skills>> getStudentSkills(@PathVariable String username) {
        return ResponseEntity.ok(studentService.getStudentSkills(username));
    }
}

