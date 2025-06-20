package org.example.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"id"})
@Schema(description = "Пользователь")
public class UserDto extends RepresentationModel<UserDto> {
    @Schema(description = "Идентификатор", example = "6", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Имя", maxLength = 250, minLength = 2, example = "Иван Петров")
    private String name;
    @Schema(description = "Почтовый адрес", maxLength = 254, minLength = 6, example = "ivan.petrov@mail.ru")
    private String email;
    @Schema(description = "Возраст", example = "18")
    private Integer age;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время создания пользователя. " +
            "Дата и время указываются в формате \"yyyy-MM-dd HH:mm:ss\"", example = "2023-10-11 23:10:05")
    private LocalDateTime createdAt;

}
