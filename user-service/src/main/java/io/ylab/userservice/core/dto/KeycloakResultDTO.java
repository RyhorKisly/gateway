package io.ylab.userservice.core.dto;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) representing a response from Keycloak service.
 * @param <T> Type parameter representing the DTO content
 */
public record KeycloakResultDTO<T> (
        T dto,
        Response response

){
}
