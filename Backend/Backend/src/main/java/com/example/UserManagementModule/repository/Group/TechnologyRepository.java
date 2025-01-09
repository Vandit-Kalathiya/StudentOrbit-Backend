package com.example.UserManagementModule.repository.Group;

import com.example.UserManagementModule.entity.Groups.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, String> {

    public Optional<Technology> findByName(String name);
}
