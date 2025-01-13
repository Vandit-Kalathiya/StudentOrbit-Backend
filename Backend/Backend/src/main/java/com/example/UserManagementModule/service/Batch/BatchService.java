package com.example.UserManagementModule.service.Batch;

import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import com.example.UserManagementModule.repository.Faculty.FacultyRepository;
import com.example.UserManagementModule.service.Group.FacultyGroupService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BatchService {

    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private FacultyGroupService facultyGroupService;

    //    @CachePut(value = "batch", key = "#batch.semester + '-' + #batch.batchName")
    public Batch saveBatch(Batch batch) {
        batch.setCreatedAt(LocalDateTime.now());
        return batchRepository.save(batch);
    }

//    @Cacheable(value = "allBatches", key = "'AllBatches'")
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

//    @Cacheable(value = "batch", key = "#id")
    public Batch getBatchById(String id) {
        return batchRepository.findById(id).orElseThrow(() -> new RuntimeException("Batch not found with id : " + id));
    }

//    @CacheEvict(value = "batch", key = "#id")
    public void deleteBatch(String batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + batchId));
    
        // First clear the faculty relationship
        if (batch.getAssignedFaculty() != null) {
            Faculty faculty = batch.getAssignedFaculty();
            batch.setAssignedFaculty(null);  // Break bidirectional relationship
            facultyRepository.save(faculty);
        }

        // Clear the groups list first
        List<String> groupIds = batch.getGroups()
                .stream()
                .map(Group::getId)
                .collect(Collectors.toList());

        // Delete groups using their IDs to avoid concurrent modification
        for (String groupId : groupIds) {
            facultyGroupService.deleteGroup(groupId);
        }

        // Clear the groups collection
        batch.getGroups().clear();

        // Now delete the batch
        batchRepository.delete(batch);
    }
//    @Cacheable(value = "batchByName", key = "#name")
    public Optional<Batch> getBatchByName(String name) {
        return Optional.of(batchRepository.getBatchByBatchName(name)
                .orElseThrow(() -> new RuntimeException("Batch not found with name : " + name)));
    }

//    @Cacheable(value = "batchByNameAndSemester", key = "#name + '-' + #semester")
    public Optional<Batch> getBatchByBatchNameAndSemester(String name, Integer semester) {
        return batchRepository.getBatchByBatchNameAndSemester(name, semester);
    }

    public List<Group> getAllGroupsOfBatch(Integer sem,String batchName){
        return batchRepository.getBatchByBatchNameAndSemester(batchName, sem).get().getGroups();
    }
}
