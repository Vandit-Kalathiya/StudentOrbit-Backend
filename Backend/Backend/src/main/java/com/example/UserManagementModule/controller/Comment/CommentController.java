package com.example.UserManagementModule.controller.Comment;

import com.example.UserManagementModule.dto.Comment.CommentRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.service.Comment.CommentService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok().body(commentService.addComment(commentRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("id") String id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }
}
