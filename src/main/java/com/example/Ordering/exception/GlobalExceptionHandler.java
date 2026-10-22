package com.example.Ordering.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", sanitizeMessage(ex.getMessage()), null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", sanitizeMessage(ex.getMessage()), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid input parameters", errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to perform this action", null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication failed: " + sanitizeMessage(ex.getMessage()), null);
    }

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<Map<String, Object>> handleDatabaseException(Exception ex) {
        // Strict guard: Never leak raw database queries, schema names or SQL stack traces to the frontend
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database Error", "A database processing error occurred. Please try again later.", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        String msg = sanitizeMessage(ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", msg, null);
    }

    private String sanitizeMessage(String message) {
        if (message == null) return "An unexpected error occurred.";
        String lower = message.toLowerCase();
        if (lower.contains("select ") || lower.contains("insert ") || lower.contains("update ") ||
            lower.contains("delete ") || lower.contains("sql") || lower.contains("jdbc") ||
            lower.contains("postgres") || lower.contains("hibernate") || lower.contains("relation ") ||
            lower.contains("column ") || lower.contains("constraint ") || lower.contains("foreign key") ||
            lower.contains("syntax error") || lower.contains("driver")) {
            return "An internal server error occurred. Please try again.";
        }
        return message;
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message, Object details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        if (details != null) {
            body.put("details", details);
        }
        return ResponseEntity.status(status).body(body);
    }
}
