package com.example.UserManagementModule.controller.Student;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

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
        if (!studentService.getStudentById(id).isPresent()) {
            throw new RuntimeException("Student not found for id : "+id);
        }
        Student student = studentService.getStudentById(id).get();
        Set<String> savedSkills = student.getSkills();
        if(savedSkills == null){
            savedSkills = new HashSet<>();
        }
        savedSkills.addAll(skills);
        student.setSkills(savedSkills);

        return new ResponseEntity<>(studentService.saveStudent(student), HttpStatus.OK);
    }

}

