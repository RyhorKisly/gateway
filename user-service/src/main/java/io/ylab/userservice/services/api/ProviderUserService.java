package io.ylab.userservice.services.api;

import io.ylab.userservice.core.dto.KeycloakResultDTO;
import io.ylab.userservice.core.dto.UserCreateDTO;
import io.ylab.userservice.core.dto.UserDTO;
import io.ylab.userservice.core.dto.UserUpdateDTO;
import io.ylab.userservice.core.enums.UserRole;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Keycloak operations related to users.
 */
public interface ProviderUserService {

    /**
     * Creates a user by Admin or some special role.
     * @param dto The UserCreateDTO containing user details
     * @return KeycloakResultDTO containing the response and user DTO
     */
    KeycloakResultDTO<UserDTO> create(UserCreateDTO dto);

    /**
     * Retrieves a user by user ID.
     * @param userId The ID of the user to retrieve
     * @return The UserRepresentation of the retrieved user
     */
    UserDTO getUserById(UUID userId);

    /**
     * Retrieves all users.
     * @return A list of UserRepresentation objects representing all users
     */
    List<UserDTO> getUsers();

    /**
     * Updates a user by user ID and new user details.
     * @param userId The ID of the user to update
     * @param dto The UserUpdateDTO containing updated user details
     * @return The UserRepresentation of the updated user
     */
    UserDTO updateUser(UUID userId, UserUpdateDTO dto);

    /**
     * Updates a user Role by user ID and new user Role.
     * @param userId The ID of the user to update
     * @param role The new user Role
     * @return The UserRepresentation of the updated user
     */
    UserDTO updateUserRole(UUID userId, UserRole role);
}
