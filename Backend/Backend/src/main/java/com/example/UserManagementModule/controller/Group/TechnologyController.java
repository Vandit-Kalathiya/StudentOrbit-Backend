package com.example.UserManagementModule.controller.Group;

import com.example.UserManagementModule.entity.Groups.Technology;
import com.example.UserManagementModule.service.Technology.TechnologyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tech")
public class TechnologyController {

    private final TechnologyService technologyService;

    public TechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    @PostMapping("/add/{groupId}")
    public ResponseEntity<Technology> addTechnology(@RequestBody String name, @PathVariable String groupId) {
        System.out.println(name);
        return new ResponseEntity<>(technologyService.add(name, groupId), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<String> deleteTechnology(@RequestBody List<String> technologiesToDelete, @PathVariable String groupId) {
        return new ResponseEntity<>(technologyService.delete(technologiesToDelete, groupId), HttpStatus.OK);
    }
}
