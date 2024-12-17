package com.example.UserManagementModule.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Sends HTTP 409 Conflict
public class TaskAssignmentException extends RuntimeException {
    public TaskAssignmentException(String message) {
        super(message);
    }
}

