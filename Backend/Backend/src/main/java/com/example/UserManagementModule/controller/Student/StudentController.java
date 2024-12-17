package com.example.UserManagementModule.controller.Student;

import com.example.UserManagementModule.dto.Student.ProfileUpdateRequest;
import com.example.UserManagementModule.dto.Student.StudentRequest;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Skills;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.repository.Skills.SkillsRepository;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
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

    @GetMapping("/u/{username}")
    public ResponseEntity<Student> getStudentByUsername(@PathVariable String username) {
        Optional<Student> student = studentService.getStudentByUsername(username);
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

    @GetMapping("/gs/{SID}")
    public ResponseEntity<List<Group>> getStudentGroupsByProjectStatus(@PathVariable String SID) {
        return ResponseEntity.ok(studentService.getStudentGroupsByProjectStatus(SID));
    }

    @PostMapping("/skills/{id}")
    public ResponseEntity<Student> addStudentSkill(@PathVariable String id, @RequestBody List<String> skills) {
        return new ResponseEntity<>(studentService.addStudentSkill(id,skills), HttpStatus.OK);
    }

    @GetMapping("/skills/{username}")
    public ResponseEntity<List<Skills>> getStudentSkills(@PathVariable String username) {
        return ResponseEntity.ok(studentService.getStudentSkills(username).stream().toList());
    }

    @DeleteMapping("/skills/{id}/{skill}")
    public ResponseEntity<Set<Skills>> deleteStudentSkill(@PathVariable String id, @PathVariable String skill) {
        return ResponseEntity.ok(studentService.deleteStudentSkill(id,skill));
    }

    @PutMapping("/profile/{username}")
    public ResponseEntity<Student> updateStudentProfile(@PathVariable String username, @RequestPart ProfileUpdateRequest profileUpdateRequest, @RequestPart MultipartFile image) throws IOException {
        return ResponseEntity.ok(studentService.updateStudentProfile(username,profileUpdateRequest,image));
    }

    @GetMapping("/{username}/image")
    public ResponseEntity<byte[]> getStudentImage(@PathVariable String username) {
        return ResponseEntity.ok().body(studentService.getProfileImage(username));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody StudentRequest studentRequest) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentRequest));
    }
}

