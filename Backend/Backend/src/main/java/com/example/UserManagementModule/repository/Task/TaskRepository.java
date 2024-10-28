package com.example.UserManagementModule.repository.Task;

import com.example.UserManagementModule.entity.Task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
}
