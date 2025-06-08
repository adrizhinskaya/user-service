package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final String ERROR_COLOR = "\u001b[31m";
    private static final String RESET = "\u001B[0m";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationExceptions(final MethodArgumentNotValidException e) {
        log.error("400 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ERROR_COLOR + e.getMessage() + RESET)
                .reason("Incorrectly made request.")
                .errors(stackTrace)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error("404 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ERROR_COLOR + e.getMessage() + RESET)
                .reason("The required object was not found.")
                .errors(stackTrace)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(EmailConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidationExceptions(final EmailConflictException e) {
        log.error("409 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(ERROR_COLOR + e.getMessage() + RESET)
                .reason("User email conflict .")
                .errors(stackTrace)
                .timestamp(LocalDateTime.now())
                .build();
    }
}