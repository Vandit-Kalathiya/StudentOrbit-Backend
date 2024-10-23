package com.example.UserManagementModule.service.Group;


import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FacultyGroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private BatchRepository batchRepository;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Optional<Group> getGroupById(String id) {
        return groupRepository.findById(id);
    }

    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroup(String id) {
        groupRepository.deleteById(id);
    }

    public Optional<Group> findGroupByName(String name) {
        return groupRepository.findByGroupName(name);
    }

    public Set<Group> getGroupsByBatch(String name){
        Set<Group> groups = groupRepository.findGroupsByBatchName(name);
        return groups;
    }

    public Optional<Group> getGroupByName(String name) {
        return groupRepository.findByGroupName(name);
    }
}
