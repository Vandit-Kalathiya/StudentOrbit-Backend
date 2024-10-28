package com.example.UserManagementModule.controller.Weeks;

import com.example.UserManagementModule.dto.Task.TaskRequest;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import com.example.UserManagementModule.service.Weeks.WeekService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/week")
public class WeeksController {

    private final GroupRepository groupRepository;
    private final WeekService weekService;

    public WeeksController(GroupRepository groupRepository, WeekService weekService) {
        this.groupRepository = groupRepository;
        this.weekService = weekService;
    }

    @Cacheable(value = "weeksOfGroup",key = "#groupName")
    @GetMapping("/{groupName}")
    public List<Week> findWeeksByGroupName(@PathVariable String groupName) {
        Group group = groupRepository.findByGroupName(groupName).get();
        List<Week> weeks = group.getWeeks();
        return weeks;
    }

    @GetMapping("/{groupId}/{weekNum}")
    public ResponseEntity<Week> getWeekByWeekNum(@PathVariable Integer weekNum,@PathVariable String groupId) {
        return ResponseEntity.ok(weekService.getWeekByWeekNum(weekNum,groupId));
    }

    @GetMapping("/w/{pName}/{weekNum}")
    public ResponseEntity<Week> getWeekByProjectName(@PathVariable Integer weekNum,@PathVariable String pName) {
        return ResponseEntity.ok(weekService.getWeekByProjectName(weekNum,pName));
    }
}
