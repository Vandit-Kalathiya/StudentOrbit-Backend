package com.example.UserManagementModule.controller.Faculty;


import com.example.UserManagementModule.dto.Faculty.FacultyRegisterRequest;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/faculty")
public class FacultyRegisterController {

    @Autowired
    FacultyService facultyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
//	@PreAuthorize("hasRole('ADMIN')")
    public List<Faculty> getAllFaculties() {
        return facultyService.findAllFaculties();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerFaculty(@RequestBody FacultyRegisterRequest facultyRequest) {
        facultyService.createFaculty(facultyRequest);
    }
}
