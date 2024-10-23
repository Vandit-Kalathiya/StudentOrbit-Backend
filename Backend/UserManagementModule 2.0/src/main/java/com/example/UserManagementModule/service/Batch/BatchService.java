package com.example.UserManagementModule.service.Batch;

import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    @Autowired
    BatchRepository batchRepository;

    public Batch saveBatch(Batch batch) {
        batch.setCreatedAt(LocalDateTime.now());

        return batchRepository.save(batch);
    }

    // Get all Batches
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    // Get a Batch by ID
    public Batch getBatchById(String id) {
        return batchRepository.findById(id).get();
    }

    // Delete a Batch by ID
    public void deleteBatch(String id) {
        batchRepository.deleteById(id);
    }

    public Optional<Batch> getBatchByName(String name) {
        return Optional.of(batchRepository.getBatchByBatchName(name).orElseThrow(() -> new RuntimeException("Batch not found with name : " + name)));
    }

    public Optional<Batch> getBatchByBatchNameAndSemester(String name, Integer semester) {
        return batchRepository.getBatchByBatchNameAndSemester(name, semester);
    }
}
