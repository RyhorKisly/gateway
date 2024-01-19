package io.ylab.userservice.core.utils;

import io.ylab.userservice.config.properties.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

/**
 * Configuration class for setting up Keycloak authentication
 */
@Component
@RequiredArgsConstructor
public class KeycloakHandler {
    private final KeycloakProperties properties;

    /**
     * Bean method to create and configure a Keycloak instance.
     * @return Configured Keycloak instance
     */
    public org.keycloak.admin.client.Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getUrlAuth())
                .realm(properties.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .build();
    }

}
