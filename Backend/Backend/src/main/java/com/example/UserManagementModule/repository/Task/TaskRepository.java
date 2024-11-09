package com.example.UserManagementModule.repository.Task;

import com.example.UserManagementModule.entity.Task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    @Query("SELECT COUNT(t) FROM Task t JOIN t.assignee s WHERE s.username = :studentId")
    Long countTasksByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(t) FROM Task t JOIN t.assignee s WHERE s.username = :studentId and t.status = :status")
    Long countTasksByStudentIdAndStatus(String studentId, String status);

    @Query("SELECT COUNT(t) FROM Task t " +
            "JOIN t.assignee s " +
            "JOIN t.week.group g " +
            "WHERE s.username = :studentId " +
            "AND t.status = :status " +
            "AND g IN (SELECT g2 FROM Group g2 JOIN g2.students s2 WHERE s2.username = :studentId and g2.projectStatus = 'IN_PROGRESS')")
    long countTasksByStudentAndStatusInGroup(@Param("studentId") String studentId,
                                             @Param("status") String status);
}
