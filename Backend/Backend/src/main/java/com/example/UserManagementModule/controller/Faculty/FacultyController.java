package com.example.UserManagementModule.controller.Faculty;

import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/faculty")
@CrossOrigin(origins = "http://localhost:5173")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Faculty>> getAllFaculty() {
        return ResponseEntity.ok(facultyService.findAllFaculties());
    }

    @PostMapping("/mentor/{facultyName}/{groupId}")
    public ResponseEntity<Group> setMentor(@PathVariable String facultyName, @PathVariable String groupId) {
        return ResponseEntity.ok(facultyService.selectMentor(facultyName, groupId));
    }


}
