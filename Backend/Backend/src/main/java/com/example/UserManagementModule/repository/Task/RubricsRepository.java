package com.example.UserManagementModule.repository.Task;

import com.example.UserManagementModule.entity.Task.Rubrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RubricsRepository extends JpaRepository<Rubrics,String> {

}
