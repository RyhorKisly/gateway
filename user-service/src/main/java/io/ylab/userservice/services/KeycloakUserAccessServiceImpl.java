package io.ylab.userservice.services;

import io.ylab.userservice.config.properties.KeycloakProperties;
import io.ylab.userservice.core.dto.UserAuthorizeDTO;
import io.ylab.userservice.services.api.UserAccessService;
import io.ylab.userservice.services.feign.TokenServiceClient;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakUserAccessServiceImpl implements UserAccessService {

    private final KeycloakProperties properties;
    private final TokenServiceClient tokenClient;

    @Override
    public AccessTokenResponse authorize(UserAuthorizeDTO dto) {
        Keycloak userKeycloak = generateUserKeycloak(dto);
        return userKeycloak.tokenManager().getAccessToken();
    }

    @Override
    public AccessTokenResponse refresh(String refreshToken) {

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN);
        requestParams.put(OAuth2Constants.CLIENT_ID, properties.getClientId());
        requestParams.put(OAuth2Constants.CLIENT_SECRET, properties.getClientSecret());
        requestParams.put(OAuth2Constants.REFRESH_TOKEN, refreshToken);

        ResponseEntity<AccessTokenResponse> response = tokenClient.refreshToken(
                requestParams,
                properties.getRealm()
        );

        return response.getBody();
    }

    /**
     * Sets up and returns a Keycloak instance based on the provided UserAuthorizeDTO credentials.
     *
     * @param dto UserAuthorizeDTO containing login and password credentials
     * @return Configured Keycloak instance
     */
    private Keycloak generateUserKeycloak(UserAuthorizeDTO dto) {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getUrlAuth())
                .realm(properties.getRealm())
                .grantType(OAuth2Constants.PASSWORD)
                .username(dto.login())
                .password(dto.password())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .build();
    }
}
