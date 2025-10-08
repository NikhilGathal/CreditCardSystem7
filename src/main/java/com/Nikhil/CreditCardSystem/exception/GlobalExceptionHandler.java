package com.Nikhil.CreditCardSystem.exception;

import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseStructure<String>> handleValidationException(ValidationException ex) {
        LOGGER.error("Validation error: {}", ex.getMessage());
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Validation failed");
//        structure.setHttpstatus(HttpStatus.BAD_REQUEST.value());
        structure.setMessage("ERROR");
        structure.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(structure);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleNotFound(ResourceNotFoundException ex) {
        LOGGER.warn("Resource not found: {}", ex.getMessage());
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Resource not found");
//        structure.setHttpstatus(HttpStatus.NOT_FOUND.value());
        structure.setMessage("ERROR");
        structure.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(structure);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseStructure<String>> handleGenericException(Exception ex) {
        LOGGER.error("Unhandled exception: {}", ex.getMessage(), ex);
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Internal server error");
//        structure.setHttpstatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        structure.setMessage("ERROR");
        structure.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(structure);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseStructure<String>> handleAccessDenied(AccessDeniedException ex) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Access Denied");
//        structure.setHttpstatus(HttpStatus.FORBIDDEN.value());
        structure.setMessage("ERROR");
        structure.setData("You don’t have permission to access this resource.");
        return new ResponseEntity<>(structure, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseStructure<String>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Access Denied");
//        structure.setHttpstatus(HttpStatus.FORBIDDEN.value());
        structure.setMessage("ERROR");
        structure.setData("You don’t have permission to access this resource.");
        return new ResponseEntity<>(structure, HttpStatus.FORBIDDEN);
    }
}

