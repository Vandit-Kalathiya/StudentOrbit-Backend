package com.example.UserManagementModule.service.Weeks;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Week.WeekRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WeekService {

    private final WeekRepository weekRepository;
    private final FacultyGroupService facultyGroupService;

    public WeekService(WeekRepository weekRepository, FacultyGroupService facultyGroupService) {
        this.weekRepository = weekRepository;
        this.facultyGroupService = facultyGroupService;
    }

    @Cacheable(value = "weeks", key = "#weekNum + '_' + #groupId")
    public Week getWeekByWeekNum(Integer weekNum, String groupId) {
        Group group = facultyGroupService.getGroupById(groupId).orElseThrow(() -> new RuntimeException("Group not found for id: " + groupId));
        return weekRepository.findByWeekNumberAndGroup(weekNum, group).orElseThrow(() -> new RuntimeException("Week not found for week number: " + weekNum + " and group: " + groupId));
    }

    @Cacheable(value = "weeks", key = "#weekNum + '_' + #pName")
    public Week getWeekByProjectName(Integer weekNum, String pName) {
        Group group = facultyGroupService.getGroupByName(pName).orElseThrow(() -> new RuntimeException("Group not found for project name: " + pName));
        return weekRepository.findByWeekNumberAndGroup(weekNum, group).orElseThrow(() -> new RuntimeException("Week not found for week number: " + weekNum + " and group: " + pName));
    }
}
