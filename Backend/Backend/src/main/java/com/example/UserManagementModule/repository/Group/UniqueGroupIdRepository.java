package com.example.UserManagementModule.repository.Group;

import com.example.UserManagementModule.entity.Groups.UniqueGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniqueGroupIdRepository extends JpaRepository<UniqueGroupId, String> {

}
