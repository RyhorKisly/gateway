package io.ylab.userservice.services.api;

import io.ylab.userservice.core.enums.UserRole;

import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for managing user CLIENT roles (NOT REALM) within Keycloak.
 */
public interface UserRoleService {

    /**
     * Retrieves the role associated with a specific user.
     *
     * @param userId User ID for whom the role needs to be retrieved
     * @return UserRole representing the user's role
     */
    Optional<UserRole> getRole(UUID userId);

    /**
     * Updates a user's role.
     *
     * @param userId User ID for whom the role will be updated
     * @param role   UserRole enum representing the updated role
     */
    UserRole updateRoleToUser(UUID userId, UserRole role);

}
