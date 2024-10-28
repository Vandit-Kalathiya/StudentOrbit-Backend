package com.example.UserManagementModule.service.Comment;

import com.example.UserManagementModule.dto.Comment.CommentRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.repository.Comment.CommentRepository;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private FacultyService facultyService;
    @Autowired
    private TaskRepository taskRepository;

    @CachePut(value = "comment", key = "#result.id")
    @CacheEvict(value = "comment", allEntries = true)
    public Comment addComment(CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setCommentDescription(commentRequest.getCommentDescription());
        System.out.println(commentRequest.getFacultyId());
        Faculty faculty = facultyService.findFacultyByFacultyName(commentRequest.getFacultyId());
        faculty.addComment(comment);
        comment.setFaculty(faculty);

        Task task = taskRepository.findById(commentRequest.getTaskId()).get();
        task.addComment(comment);
        comment.setTask(task);

        comment.setDate(LocalDate.now());
        comment.setTime(LocalTime.now());

        return commentRepository.save(comment);
    }

    @Cacheable(value = "comment",key = "#id")
    public Comment getCommentById(String id) {
        return commentRepository.findById(id).get();
    }
}
