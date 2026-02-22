package com.cmm.mit.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handling for the API.
 *
 * <p>Returns RFC 7807 Problem Details responses ({@link org.springframework.http.ProblemDetail})
 * with a consistent {@code errorCode} extension field.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle bean validation errors (400) and return field-level details.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Validation failed");
    problem.setDetail("Invalid request");
    problem.setType(URI.create("https://example.com/problems/validation"));
    problem.setInstance(URI.create(request.getRequestURI()));

    // Build a lightweight, UI-friendly field error list.
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> {
          Map<String, Object> m = new HashMap<>();
          m.put("field", fe.getField());
          m.put("message", fe.getDefaultMessage());
          return m;
        })
        .toList();

    problem.setProperty("errorCode", "VALIDATION_ERROR");
    problem.setProperty("errors", errors);

    return ResponseEntity.badRequest().body(problem);
  }

  /**
   * Handle domain-level bad requests (400).
   */
  @ExceptionHandler(BadRequestException.class)
  ResponseEntity<ProblemDetail> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Bad request");
    problem.setType(URI.create("https://example.com/problems/bad-request"));
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("errorCode", "BAD_REQUEST");
    return ResponseEntity.badRequest().body(problem);
  }

  /**
   * Handle resource-not-found cases (404).
   */
  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Not found");
    problem.setType(URI.create("https://example.com/problems/not-found"));
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("errorCode", "NOT_FOUND");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
  }

  /**
   * Catch-all handler for unexpected errors (500).
   */
  @ExceptionHandler(Exception.class)
  ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    problem.setTitle("Internal server error");
    problem.setType(URI.create("https://example.com/problems/internal-error"));
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("errorCode", "INTERNAL_ERROR");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }
}
