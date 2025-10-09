package com.Nikhil.CreditCardSystem.exception;

import com.Nikhil.CreditCardSystem.service.UserActionLogService;
import com.Nikhil.CreditCardSystem.util.ResponseStructure;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private UserActionLogService userActionLogService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseStructure<String>> handleValidationException(ValidationException ex) {
        LOGGER.error("Validation error: {}", ex.getMessage());
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Validation failed");
//        structure.setHttpstatus(HttpStatus.BAD_REQUEST.value());
        structure.setHttpstatus("ERROR");
        structure.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(structure);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleNotFound(ResourceNotFoundException ex) {
        LOGGER.warn("Resource not found: {}", ex.getMessage());
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Resource not found");
//        structure.setHttpstatus(HttpStatus.NOT_FOUND.value());
        structure.setHttpstatus("ERROR");
        structure.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(structure);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseStructure<String>> handleAccessDenied(AccessDeniedException ex) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Access Denied");
//        structure.setHttpstatus(HttpStatus.FORBIDDEN.value());
        structure.setHttpstatus("ERROR");
        structure.setData("You don’t have permission to access this resource.");
        return new ResponseEntity<>(structure, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseStructure<String>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Access Denied");
//        structure.setHttpstatus(HttpStatus.FORBIDDEN.value());
        structure.setHttpstatus("ERROR");
        structure.setData("You don’t have permission to access this resource.");
        return new ResponseEntity<>(structure, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ResponseStructure<String>> handleInvalidInputException(InvalidInputException ex) {
        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage(ex.getMessage());
        structure.setHttpstatus("FAILED");
        structure.setData("Invalid input data");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(structure);
    }



    // Handles invalid URLs (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handle404(NoHandlerFoundException ex, HttpServletRequest request) {
        userActionLogService.logAction(
                "UNKNOWN_USER", // user info unknown
                "Invalid URL requested: " + request.getRequestURI(),
                "FAILED"
        );

        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("URL not found: " + request.getRequestURI());
        structure.setHttpstatus("FAILED");
        structure.setData("The requested URL could not be found.");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(structure);
    }


    // Handles validation errors (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseStructure<String>> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        userActionLogService.logAction(
                "UNKNOWN_USER",
                "Validation failed on URL: " + request.getRequestURI(),
                "FAILED"
        );

        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("Validation error: " + errorMessage);
        structure.setHttpstatus("FAILED");
        structure.setData("Validation failed for request on URL: " + request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(structure);
    }




    // Catch all other errors (500, etc.)

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseStructure<String>> handleAllExceptions(Exception ex, HttpServletRequest request) {
        userActionLogService.logAction(
                "UNKNOWN_USER",
                "Error on URL: " + request.getRequestURI() + " - " + ex.getMessage(),
                "FAILED"
        );

        ResponseStructure<String> structure = new ResponseStructure<>();
        structure.setMessage("An error occurred: " + ex.getMessage());
        structure.setHttpstatus("FAILED");
        structure.setData("An unexpected error occurred while processing the request.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(structure);
    }
}

