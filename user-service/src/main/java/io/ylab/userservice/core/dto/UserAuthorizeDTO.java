package io.ylab.userservice.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO representing user authorization credentials.
 */
public record UserAuthorizeDTO(
        @NotBlank(message = "Login is mandatory")
        String login,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 3, message = "Password too short. Must be more 2 symbols")
        @Size(max = 20, message = "Password too long. Must be less 21 symbols")
        String password
) {

}
