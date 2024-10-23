package com.example.UserManagementModule.service.Comment;

import com.example.UserManagementModule.dto.Comment.CommentRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.entity.Weeks.Week;
import com.example.UserManagementModule.repository.Comment.CommentRepository;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Comment addComment(CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setCommentDescription(commentRequest.getCommentDescription());
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

    public Comment getCommentById(String id) {
        return commentRepository.findById(id).get();
    }
}
