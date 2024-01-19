package io.ylab.userservice.core.dto;

import io.ylab.userservice.core.enums.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDTO(
        UUID id,
        String login,
        String email,
        UserRole role,
        String fio) {
}
