package org.example.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ApiError {
    private String errors;
    private String message;
    private String reason;
    private HttpStatus status;
    private LocalDateTime timestamp;
}