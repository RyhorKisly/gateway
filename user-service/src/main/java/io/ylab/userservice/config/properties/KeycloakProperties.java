package io.ylab.userservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Keycloak authentication.
 */
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {

    /**
     * The realm in Keycloak.
     */
    private String realm;

    /**
     * The client ID used for authentication.
     */
    private String clientId;

    /**
     * The client secret used for authentication.
     */
    private String clientSecret;

    /**
     * The URL for Keycloak authentication.
     */
    private String urlAuth;
}
