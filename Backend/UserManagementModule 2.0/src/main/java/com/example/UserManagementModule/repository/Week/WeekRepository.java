package com.example.UserManagementModule.repository.Week;

import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.entity.Weeks.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeekRepository extends JpaRepository<Week, String> {

    Optional<Week> findByWeekNumberAndGroup(Integer weekNo, Group group);
}
