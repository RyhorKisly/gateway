package io.ylab.userservice.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO representing a token.
 */
@Builder
public record TokenDTO(
        @NotBlank(message = "Refresh token is mandatory")
        String token
) {

}
