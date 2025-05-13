package com.example.UserManagementModule.controller.Student;


import com.example.UserManagementModule.dto.Student.StudentResgisterRequest;
import com.example.UserManagementModule.entity.Student.Student;
import com.example.UserManagementModule.service.Student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/student")
public class StudentRegisterController {

    private final StudentService studentService;

    @Autowired
    public StudentRegisterController(StudentService studentService) {
        this.studentService = studentService;
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//	@PreAuthorize("hasRole('ADMIN')")
//    public List<Student> getAllStudents() {
//        return studentService.getAllStudents();
//    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerStudent(@RequestBody StudentResgisterRequest studentRequest) {
        studentService.createStudent(studentRequest);
    }
}
