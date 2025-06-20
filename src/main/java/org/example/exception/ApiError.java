package org.example.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Schema(description = "Сведения об ошибке")
public class ApiError {
    @Schema(description = "Список стектрейсов или описания ошибок")
    private String errors;
    @Schema(description = "Сообщение об ошибке", example = "Field: name. Error: must not be blank. Value: null")
    private String message;
    @Schema(description = "Общее описание причины ошибки", example = "Incorrectly made request.")
    private String reason;
    @Schema(description = "Код статуса HTTP-ответа", example = "BAD_REQUEST")
    private HttpStatus status;
    @Schema(description = "Дата и время когда произошла ошибка (в формате \"yyyy-MM-dd HH:mm:ss\")",
            example = "2022-09-07 09:10:50")
    private LocalDateTime timestamp;
}