package io.ylab.userservice.services;

import io.ylab.userservice.config.properties.KeycloakProperties;
import io.ylab.userservice.core.enums.UserRole;
import io.ylab.userservice.core.utils.KeycloakHandler;
import io.ylab.userservice.services.api.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakUserRoleServiceImpl implements UserRoleService {
    private final KeycloakProperties properties;
    private final KeycloakHandler keycloak;
    private RoleRepresentation role;
    @Override
    public Optional<UserRole> getRole(UUID userId) {
        RealmResource realm = keycloak.keycloak().realm(properties.getRealm());
        UsersResource usersResource = realm.users();
        UserResource userResource = usersResource.get(userId.toString());

        List<ClientRepresentation> clientRepresentations = realm.clients().findByClientId(properties.getClientId());
        ClientRepresentation clientRepresentation = findClientRepresentation(clientRepresentations);

        return findUserRole(clientRepresentation, userResource);
    }

    @Override
    public UserRole updateRoleToUser(UUID userId, UserRole userRole) {
        RealmResource realm = keycloak.keycloak().realm(properties.getRealm());
        UsersResource usersResource = realm.users();
        UserResource userResource = usersResource.get(userId.toString());
        ClientRepresentation clientRep = realm.clients().findByClientId(properties.getClientId()).get(0);
        List<RoleRepresentation> clientRoles = userResource.roles().clientLevel(clientRep.getId()).listAll();

        deleteRoleIfExist(clientRoles, realm, userResource, clientRep);

        role = realm.clients().get(clientRep.getId()).roles().get(userRole.name()).toRepresentation();
        userResource.roles().clientLevel(clientRep.getId()).add(Collections.singletonList(role));
        return userRole;
    }

    private ClientRepresentation findClientRepresentation(List<ClientRepresentation> clientRepresentations) {
        ClientRepresentation clientRepresentation = null;
        for (ClientRepresentation client : clientRepresentations) {
            if(client.getClientId().equals(properties.getClientId())) {
                clientRepresentation = client;
            }
        }
        return clientRepresentation;
    }

    private Optional<UserRole> findUserRole(ClientRepresentation clientRepresentation, UserResource userResource) {
        if(clientRepresentation == null) {
            return Optional.empty();
        } else {
            List<RoleRepresentation> clientRoles = userResource.roles().clientLevel(clientRepresentation.getId()).listAll();
            return Optional.ofNullable(extractRoleIfExist(clientRoles));
        }
    }

    /**
     * Checks if any of the UserRole values exist in the provided list of RoleRepresentations.
     *
     * @param clientRoles List of RoleRepresentations to search for UserRole values
     * @return UserRole if found, otherwise null
     */
    private UserRole extractRoleIfExist(List<RoleRepresentation> clientRoles) {
        for (UserRole value : UserRole.values()) {
            for (RoleRepresentation clientRole : clientRoles) {
                if (clientRole.getName().equals(value.name())) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Deletes the roles if any of the UserRole values exist in the provided list of RoleRepresentations.
     *
     * @param clientRoles List of RoleRepresentations to check for UserRole values
     * @param realm RealmResource instance
     * @param userResource UserResource instance, remove role
     * @param clientRep ClientRepresentation instance, contains client roles
     */
    private void deleteRoleIfExist(
            List<RoleRepresentation> clientRoles,
            RealmResource realm,
            UserResource userResource,
            ClientRepresentation clientRep
    ) {
        for (UserRole value : UserRole.values()) {
            for (RoleRepresentation clientRole : clientRoles) {
                if (clientRole.getName().equals(value.name())) {
                    role = realm.clients().get(clientRep.getId()).roles().get(clientRole.getName()).toRepresentation();
                    userResource.roles().clientLevel(clientRep.getId()).remove(Collections.singletonList(role));
                }
            }
        }
    }
}

