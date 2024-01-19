package io.ylab.userservice.core.dto;

import io.ylab.userservice.core.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO representing user update details.
 */
public record UserUpdateDTO(
        @NotBlank(message = "login is mandatory")
        String login,
        @Email
        @NotBlank(message = "Email is mandatory")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 3, message = "Password too short. Must be more 2 symbols")
        @Size(max = 20, message = "Password too long. Must be less 21 symbols")
        String password,
        @NotNull(message = "Role is mandatory")
        UserRole role,
        @NotBlank(message = "FIO is mandatory")
        String fio
) {
}
