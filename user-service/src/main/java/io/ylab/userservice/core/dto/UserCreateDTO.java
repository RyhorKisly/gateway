package io.ylab.userservice.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO representing user creation details.
 */
public record UserCreateDTO(
        @NotBlank(message = "login is mandatory")
        String login,
        @Email
        @NotBlank(message = "Email is mandatory")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 3, message = "Password too short. Must be more 2 symbols")
        @Size(max = 20, message = "Password too long. Must be less 21 symbols")
        String password,
        @NotBlank(message = "FIO is mandatory")
        String fio
) {
}
