package org.example.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "Данные нового пользователя")
public class NewUserDto {
    @NotBlank(message = "User name must not be blank")
    @Size(min = 2, message = "User name must be longer than 2 symbols")
    @Size(max = 250, message = "User name must be shorter than 250 symbols")
    @Schema(description = "Имя", maxLength = 250, minLength = 2, example = "Иван Петров")
    private String name;
    @NotBlank(message = "User email must not be blank")
    @Size(min = 6, message = "User email must be longer than 6 symbols")
    @Size(max = 254, message = "User email must be shorter than 254 symbols")
    @Email
    @Schema(description = "Почтовый адрес", maxLength = 254, minLength = 6, example = "ivan.petrov@mail.ru")
    private String email;
    @NotNull
    @Positive(message = "User age must be positive")
    @Schema(description = "Возраст", example = "18")
    private Integer age;
}
