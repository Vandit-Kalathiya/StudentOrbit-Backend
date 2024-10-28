package com.example.UserManagementModule.repository.Comment;

import com.example.UserManagementModule.entity.Comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

}
