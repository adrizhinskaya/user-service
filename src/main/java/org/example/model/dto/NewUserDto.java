package org.example.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewUserDto {
    @NotBlank(message = "User name must not be blank")
    @Size(min = 2, message = "User name must be longer than 2 symbols")
    @Size(max = 250, message = "User name must be shorter than 250 symbols")
    private String name;
    @NotBlank(message = "User email must not be blank")
    @Size(min = 6, message = "User email must be longer than 6 symbols")
    @Size(max = 254, message = "User email must be shorter than 254 symbols")
    @Email
    private String email;
    @NotNull
    @Positive(message = "User age must be positive")
    private Integer age;
}
