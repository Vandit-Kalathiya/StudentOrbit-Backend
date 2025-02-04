package com.example.UserManagementModule.service.Technology;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Groups.Technology;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import com.example.UserManagementModule.repository.Technology.TechnologyRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TechnologyService {

    @Autowired
    private TechnologyRepository technologyRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private FacultyGroupService facultyGroupService;

    public Technology add(String name, String groupId) {
        name = name.substring(1,name.length()-1);
        System. out.println(name);
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if(optionalGroup.isPresent()) {
            Group group  = optionalGroup.get();
            List<Technology> technologies = group.getTechnologies();
            Technology technology = technologyRepository.save(Technology.builder().name(name).build());
            technologies.add(technology);
            facultyGroupService.saveGroup(group);
            return technology;
        }
        throw new EntityNotFoundException("Group with id " + groupId + " does not exist");
    }

    public String delete(List<String> deleteList, String groupId) {
        // Get the group
        Group group = facultyGroupService.getGroupById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        // Get current technologies
        List<Technology> allTechnologies = group.getTechnologies();

        // Create list for technologies to be completely deleted
        List<Technology> technologiesToDelete = allTechnologies.stream()
                .filter(tech -> deleteList.contains(tech.getId()))
                .collect(Collectors.toList());

        // Create list for technologies to keep
        List<Technology> technologiesToKeep = allTechnologies.stream()
                .filter(tech -> !deleteList.contains(tech.getId()))
                .collect(Collectors.toList());

        // Update group's technologies
        group.setTechnologies(technologiesToKeep);
        facultyGroupService.saveGroup(group);

        // Get all groups to check if technologies are used elsewhere
        List<Group> allGroups = facultyGroupService.getAllGroups();

        // Delete technologies that aren't used by any other group
        for (Technology tech : technologiesToDelete) {
            boolean isUsedInOtherGroups = allGroups.stream()
                    .filter(g -> !g.getId().equals(groupId)) // Exclude current group
                    .anyMatch(g -> g.getTechnologies().stream()
                            .anyMatch(t -> t.getId().equals(tech.getId()))
                    );

            if (!isUsedInOtherGroups) {
                technologyRepository.deleteById(tech.getId());
            }
        }

        return "Technologies Deleted Successfully";
    }
}
