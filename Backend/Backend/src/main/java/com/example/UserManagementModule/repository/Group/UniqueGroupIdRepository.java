package com.example.UserManagementModule.repository.Group;

import com.example.UserManagementModule.entity.Groups.UniqueGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UniqueGroupIdRepository extends JpaRepository<UniqueGroupId, String> {

    public Optional<UniqueGroupId> findByUniqueGroupId(String uniqueGroupId);
}
