package io.ylab.userservice.endpoints.controller;

import io.ylab.userservice.core.dto.TokenDTO;
import io.ylab.userservice.core.dto.UserAuthorizeDTO;
import io.ylab.userservice.services.api.UserAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing user access operations.
 */
@RestController
@RequiredArgsConstructor
public class UseAccessController {

    /**
     * Service interface for handling user access related operations.
     */
    private final UserAccessService accessService;

    /**
     * Authorizes a user.
     *
     * @param dto The UserAuthorizeDTO containing user authorization details
     * @return ResponseEntity containing the authorization token
     */
    @PostMapping("/auth")
    public ResponseEntity<AccessTokenResponse> authorize(
            @RequestBody @Valid UserAuthorizeDTO dto
    ) {
        AccessTokenResponse response = accessService.authorize(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint to refresh an access token.
     *
     * @param refreshToken DTO containing the refresh token
     * @return ResponseEntity with the refreshed access token
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @RequestBody @Valid TokenDTO refreshToken
            ) {
        return new ResponseEntity<>(accessService.refresh(refreshToken.token()), HttpStatus.OK);

    }

}
