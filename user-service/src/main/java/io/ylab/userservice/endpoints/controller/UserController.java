package io.ylab.userservice.endpoints.controller;

import io.ylab.userservice.core.dto.KeycloakResultDTO;
import io.ylab.userservice.core.dto.UserCreateDTO;
import io.ylab.userservice.core.dto.UserDTO;
import io.ylab.userservice.core.dto.UserUpdateDTO;
import io.ylab.userservice.core.enums.UserRole;
import io.ylab.userservice.services.api.ProviderUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller to manage user-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    /**
     * Service interface for Keycloak operations related to users.
     */
    private final ProviderUserService providerUserService;

    /**
     * Registers a new user.
     *
     * @param dto The UserCreateDTO containing user details
     * @return ResponseEntity containing the registered user's details
     */
    @PostMapping
    public ResponseEntity<UserDTO> create(
            @RequestBody @Valid UserCreateDTO dto
    ) {
        KeycloakResultDTO<UserDTO> responseDto = providerUserService.create(dto);
        return new ResponseEntity<>(
                responseDto.dto(),
                HttpStatusCode.valueOf(responseDto.response().getStatus())
        );
    }

    /**
     * Retrieves the current user. userId gets from url
     * @return ResponseEntity containing the current user representation
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(
            @PathVariable UUID id
    ) {
        UserDTO userDTO = providerUserService.getUserById(id);


        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Retrieves all users.
     * @return ResponseEntity containing a list of user representations
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        return new ResponseEntity<>(providerUserService.getUsers(), HttpStatus.OK);
    }

    /**
     * Updates a user by userId by Admin
     * @param id The UUID of the user to update
     * @param userUpdateDTO The updated user data
     * @return ResponseEntity containing the updated user representation
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO
    ) {
        UserDTO user = providerUserService.updateUser(id, userUpdateDTO);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Updates a user's role.
     *
     * @param id   User ID
     * @param role UserRole enum representing the updated role
     * @return ResponseEntity indicating success or failure
     */
    @PatchMapping("/{id}/roles/{role}")
    public ResponseEntity<UserDTO> updateRoleToUser(
            @PathVariable UUID id,
            @PathVariable UserRole role
    ) {
        UserDTO userDTO = providerUserService.updateUserRole(id, role);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

}
