package com.example.UserManagementModule.service.Group;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import com.example.UserManagementModule.repository.Group.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

//    @Cacheable(value = "allGroups")
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

//    @Cacheable(value = "groupById", key = "#id")
    public Optional<Group> getGroupById(String id) {
        return groupRepository.findById(id);
    }

//    @CacheEvict(value = "allGroups", allEntries = true)
    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

//    @CacheEvict(value = "allGroups", allEntries = true)
    public void deleteGroup(String id) {
        groupRepository.deleteById(id);
    }

//    @Cacheable(value = "groupByName", key = "#name")
    public Optional<Group> getGroupByName(String name) {
        return groupRepository.findByGroupName(name);
    }

//    @Cacheable(value = "groupsByBatch", key = "#name")
    public Set<Group> getGroupsByBatch(String name) {
        return groupRepository.findGroupsByBatchName(name);
    }


}


