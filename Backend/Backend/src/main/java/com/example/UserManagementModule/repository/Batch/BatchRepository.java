package com.example.UserManagementModule.repository.Batch;

import com.example.UserManagementModule.entity.Batches.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {

    public Optional<Batch> getBatchByBatchName(String batchName);

    public Optional<Batch> getBatchByBatchNameAndSemester(String batchName, Integer sem);
}
