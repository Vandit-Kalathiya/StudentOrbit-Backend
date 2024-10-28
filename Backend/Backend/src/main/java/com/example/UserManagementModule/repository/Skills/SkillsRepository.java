package com.example.UserManagementModule.repository.Skills;

import com.example.UserManagementModule.entity.Student.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, String> {
    public Skills findByName(String name);
}
