package io.ylab.userservice.services;

import io.ylab.userservice.config.properties.KeycloakProperties;
import io.ylab.userservice.core.dto.KeycloakResultDTO;
import io.ylab.userservice.core.dto.UserCreateDTO;
import io.ylab.userservice.core.dto.UserDTO;
import io.ylab.userservice.core.dto.UserUpdateDTO;
import io.ylab.userservice.core.enums.UserRole;
import io.ylab.userservice.core.exceptions.RoleNotExistException;
import io.ylab.userservice.core.utils.KeycloakHandler;
import io.ylab.userservice.services.api.UserRoleService;
import io.ylab.userservice.services.api.ProviderUserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements ProviderUserService {

    private final KeycloakProperties properties;
    private final UserRoleService userRoleService;
    private final KeycloakHandler keycloak;
    private static final String FIO = "fio";
    private static final String ROLE_NOT_EXIST = "User does not have a role!";

    @Override
    public KeycloakResultDTO<UserDTO> create(UserCreateDTO dto) {
        List<CredentialRepresentation> credentials = generateCredentialRepresentations(dto.password());
        UserRepresentation user = generateUserRepresentation(dto, credentials);

        RealmResource realm = keycloak.keycloak().realm(properties.getRealm());

        UsersResource usersResource =  realm.users();
        Response response = usersResource.create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);

        UserRole role = userRoleService.updateRoleToUser(UUID.fromString(userId), UserRole.USER);

        UserDTO userDTO = generateUserDTO(dto, role, userId);

        return new KeycloakResultDTO<>(userDTO, response);
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        RealmResource realm = keycloak.keycloak().realm(properties.getRealm());
        UsersResource usersResource = realm.users();
        UserResource userResource = usersResource.get(userId.toString());
        UserRepresentation user = userResource.toRepresentation();

        return generateUserDTO(user);
    }

    @Override
    public List<UserDTO> getUsers() {
        RealmResource realm = keycloak.keycloak().realm(properties.getRealm());
        UsersResource usersResource = realm.users();
        List<UserRepresentation> users = usersResource.list();
        List<UserDTO> dtos = new ArrayList<>();

        for (UserRepresentation user : users) {
            dtos.add(generateUserDTO(user));
        }

        return dtos;
    }

    @Override
    public UserDTO updateUser(UUID userId, UserUpdateDTO userUpdateDTO) {
        RealmResource realmResource = keycloak.keycloak().realm(properties.getRealm());
        UsersResource usersResource = realmResource.users();

        UserRepresentation user = usersResource.get(userId.toString()).toRepresentation();

        List<CredentialRepresentation> credentials = generateCredentialRepresentations(userUpdateDTO.password());
        updateUserRepresentation(userUpdateDTO, credentials, user);
        usersResource.get(userId.toString()).update(user);

        String fio = extractFioAttribute(user);

        UserDTO userDTO = UserDTO.builder()
                .id(UUID.fromString(user.getId()))
                .fio(fio)
                .login(user.getUsername())
                .email(user.getEmail())
                .role(userUpdateDTO.role())
                .build();

        userRoleService.updateRoleToUser(userId, userUpdateDTO.role());

        return userDTO;
    }

    @Override
    public UserDTO updateUserRole(UUID userId, UserRole role) {
        userRoleService.updateRoleToUser(userId, role);
        return getUserById(userId);
    }

    /**
     * Sets up a UserRepresentation object based on the provided UserCreateDTO and credentials.
     * The role is assigned by user
     *
     * @param dto The UserCreateDTO containing user details
     * @param credentials The list of CredentialRepresentation objects for the user
     * @return The configured UserRepresentation object
     */
    private UserRepresentation generateUserRepresentation(UserCreateDTO dto, List<CredentialRepresentation> credentials) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(dto.login());
        user.setEmail(dto.email());
        user.setEmailVerified(true);
        user.singleAttribute(FIO, dto.fio());
        user.setCredentials(credentials);
        return user;
    }

    private UserDTO generateUserDTO(UserCreateDTO dto, UserRole role, String userId) {
        return UserDTO.builder()
                .id(UUID.fromString(userId))
                .fio(dto.fio())
                .login(dto.login())
                .email(dto.email())
                .role(role)
                .build();
    }

    private UserDTO generateUserDTO(UserRepresentation user) {
        String fio = extractFioAttribute(user);
        UserRole userRole = userRoleService.getRole(UUID.fromString(user.getId()))
                .orElseThrow(() -> new RoleNotExistException(ROLE_NOT_EXIST));

        return UserDTO.builder()
                .id(UUID.fromString(user.getId()))
                .fio(fio)
                .login(user.getUsername())
                .email(user.getEmail())
                .role(userRole)
                .build();
    }

    private void updateUserRepresentation(
            UserUpdateDTO dto, List<CredentialRepresentation> credentials, UserRepresentation user
    ) {
        user.setEnabled(true);
        user.setUsername(dto.login());
        user.setEmail(dto.email());
        user.setEmailVerified(true);
        user.singleAttribute(FIO, dto.fio());
        user.setCredentials(credentials);
    }

    /**
     * Sets up CredentialRepresentation objects based on the provided UserCreateDTO.
     *
     * @param password of the User containing user details
     * @return The list of CredentialRepresentation objects
     */
    private List<CredentialRepresentation> generateCredentialRepresentations(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        // Учётных данных (Credentials) может быть несколько, потому их нужно добавлять списком
        List<CredentialRepresentation> credentials = new ArrayList<>();
        credentials.add(credentialRepresentation);
        return credentials;
    }

    /**
     * Extract the FIO attribute from the user representation.
     *
     * @param user The user representation
     * @return The value of the FIO attribute
     */
    private String extractFioAttribute(UserRepresentation user) {
        Map<String, List<String>> attributes = user.getAttributes();
        List<String> attributeFio;
        String fio = "";
        if(attributes != null) {
            if (attributes.containsKey("fio")) {
                attributeFio = attributes.get("fio");
                fio = attributeFio.get(0);
            }
        }
        return fio;
    }

}
