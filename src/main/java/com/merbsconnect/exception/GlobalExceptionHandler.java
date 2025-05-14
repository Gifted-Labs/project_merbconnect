package com.merbsconnect.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.merbsconnect")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorDetails> handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "TOKEN_REFRESH_ERROR"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorDetails> handleEmailAlreadyExistException(EmailAlreadyExistException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "EMAIL_ALREADY_EXIST");

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "USER_NOT_FOUND"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDetails> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "INVALID_TOKEN"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorDetails> handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "TOKEN_EXPIRED"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.GONE);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "USERNAME_NOT_FOUND"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "BAD_CREDENTIALS"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation errors: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @AllArgsConstructor
    @Data
    public static class ErrorDetails{
        private LocalDateTime timestamp;
        private String message;
        private String path;
        private String errorCode;
    }
}



