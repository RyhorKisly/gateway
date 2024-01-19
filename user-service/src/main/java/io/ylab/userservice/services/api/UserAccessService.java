package io.ylab.userservice.services.api;

import io.ylab.userservice.core.dto.UserAuthorizeDTO;
import org.keycloak.representations.AccessTokenResponse;

/**
 * Service interface for Keycloak access operations.
 */
public interface UserAccessService {

    /**
     * Authorizes a user.
     * @param dto The UserAuthorizeDTO containing user authorization details
     * @return The authorization token
     */
    AccessTokenResponse authorize(UserAuthorizeDTO dto);

    /**
     * Refreshes an access token.
     * @param refreshToken The refresh token
     * @return The new access token
     */
    AccessTokenResponse refresh(String refreshToken);

}
