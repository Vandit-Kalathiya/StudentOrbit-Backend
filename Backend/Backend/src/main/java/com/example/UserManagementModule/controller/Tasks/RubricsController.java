package com.example.UserManagementModule.controller.Tasks;

import com.example.UserManagementModule.dto.Task.RubricRequest;
import com.example.UserManagementModule.entity.Task.Rubrics;
import com.example.UserManagementModule.service.Tasks.RubricsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rubrics")
public class RubricsController {

    private final RubricsService rubricsService;

    public RubricsController(RubricsService rubricsService) {
        this.rubricsService = rubricsService;
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<Rubrics> addRubric(@RequestBody RubricRequest rubricRequest, @PathVariable String taskId) {
        return new ResponseEntity<>(rubricsService.addRubrics(rubricRequest.getRubricName(), rubricRequest.getRubricScore(), taskId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rubrics> getRubricById(@PathVariable String id) {
        return ResponseEntity.ok(rubricsService.getRubricsById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRubric(@PathVariable String id) {
        rubricsService.deleteRubrics(id);
        return ResponseEntity.noContent().build();
    }
}
