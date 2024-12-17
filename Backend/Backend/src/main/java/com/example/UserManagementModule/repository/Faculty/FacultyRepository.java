package com.example.UserManagementModule.repository.Faculty;

import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, String> {

    Optional<Faculty> findByUsername(String username);

    Optional<Faculty> findByName(String name);
}
