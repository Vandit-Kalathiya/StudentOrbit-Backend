package com.example.UserManagementModule.service.Technology;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Groups.Technology;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import com.example.UserManagementModule.repository.Technology.TechnologyRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnologyService {

    @Autowired
    private TechnologyRepository technologyRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private FacultyGroupService facultyGroupService;

    public Technology add(String name, String groupId) {
        name = name.substring(0,1).toUpperCase() + name.substring(1,name.length()-1).toLowerCase();
        System.out.println(name);
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if(optionalGroup.isPresent()) {
            Group group  = optionalGroup.get();
            List<Technology> technologies = group.getTechnologies();
            Technology technology = technologyRepository.save(Technology.builder().name(name.trim()).build());
            technologies.add(technology);
            facultyGroupService.saveGroup(group);
            return technology;
        }
        throw new EntityNotFoundException("Group with id " + groupId + " does not exist");
    }
}
