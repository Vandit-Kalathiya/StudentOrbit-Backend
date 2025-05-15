package com.example.UserManagementModule.controller.Comment;

import com.example.UserManagementModule.dto.Comment.CommentRequest;
import com.example.UserManagementModule.entity.Comment.Comment;
import com.example.UserManagementModule.service.Comment.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest) {
        try {
            if (commentRequest == null || commentRequest.getCommentDescription() == null || commentRequest.getCommentDescription().trim().isEmpty()) {
                logger.warn("Invalid comment request: {}", commentRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Comment request cannot be null or have empty content");
            }

            Comment comment = commentService.addComment(commentRequest);
            logger.info("Comment added successfully: {}", comment.getId());
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for adding comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding comment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add comment: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                logger.warn("Invalid comment ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Comment ID cannot be null or empty");
            }

            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                logger.warn("Comment not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Comment not found with ID: " + id);
            }

            logger.info("Retrieved comment: {}", id);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            logger.error("Error retrieving comment with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve comment: " + e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}/{taskId}")
    public ResponseEntity<?> removeComment(@PathVariable String commentId, @PathVariable String taskId) {
        try {
            if (commentId == null || commentId.trim().isEmpty()) {
                logger.warn("Invalid comment ID: {}", commentId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Comment ID cannot be null or empty");
            }
            if (taskId == null || taskId.trim().isEmpty()) {
                logger.warn("Invalid task ID: {}", taskId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Task ID cannot be null or empty");
            }

            String result = commentService.deleteCommentById(commentId, taskId);
            logger.info("Comment deleted successfully: {}", commentId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for deleting comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting comment with ID {} for task {}: {}", commentId, taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete comment: " + e.getMessage());
        }
    }
}