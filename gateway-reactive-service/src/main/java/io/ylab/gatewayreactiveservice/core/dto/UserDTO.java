package io.ylab.gatewayreactiveservice.core.dto;

import lombok.Builder;

@Builder
public record UserDTO(String id, String role) {
}
