package com.example.UserManagementModule.repository.Group;

import com.example.UserManagementModule.entity.Groups.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    public Optional<Group> findByGroupName(String name);

    public Set<Group> findGroupsByBatchName(String batch);

    @Query("SELECT g FROM Group g JOIN g.students s WHERE s.username = :sid")
    public List<Group> findGroupsByStudentId(@Param("sid") String sid);

    @Query("SELECT g FROM Group g JOIN g.students s WHERE s.username = :sid and g.projectStatus = 'IN_PROGRESS'")
    public List<Group> findGroupsByStudentIdAndProjectStatus(@Param("sid") String sid);
}
