package com.example.UserManagementModule.repository.Batch;

import com.example.UserManagementModule.entity.Batches.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {

    public Optional<Batch> getBatchByBatchName(String batchName);

    public Optional<Batch> getBatchByBatchNameAndSemester(String batchName, Integer sem);

    @Query("SELECT b FROM Batch b WHERE b.assignedFaculty.username = :facultyName")
    Optional<List<Batch>> findAllBatchesOfFaculty(@Param("facultyName") String facultyName);
}
