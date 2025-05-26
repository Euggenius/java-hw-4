package com.hse.fileanalysisservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, WebRequest request, String details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (details != null && !details.isEmpty()) {
            body.put("details", details);
        }
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
     private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        return buildErrorResponse(message, status, request, null);
     }


    @ExceptionHandler(AnalysisException.class)
    public ResponseEntity<Object> handleAnalysisException(AnalysisException ex, WebRequest request) {
        logger.warn("Analysis processing error: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<Object> handleRemoteServiceException(RemoteServiceException ex, WebRequest request) {
        logger.error("Remote service communication error: {}", ex.getMessage(), ex.getCause());
        return buildErrorResponse(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        logger.warn("ResponseStatusException: {} - {}", ex.getStatus(), ex.getReason());
        return buildErrorResponse(ex.getReason(), ex.getStatus(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.warn("Validation error: {}", errors);
        return buildErrorResponse("Validation failed.", HttpStatus.BAD_REQUEST, request, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected exception: {}", ex.getMessage(), ex);
        return buildErrorResponse("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}