package io.ylab.userservice.services.feign;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign client interface for handling token-related operations with Keycloak.
 */
@FeignClient(name = "tokenClient", url = "${keycloak.url-auth}")
public interface TokenServiceClient {

    /**
     * Sends a POST request to refresh the access token using the refresh token in Keycloak.
     * @param requestParams Request parameters in the form of a map containing grant_type and refresh_token.
     * @param realm The Keycloak realm where the token refresh is performed.
     * @return ResponseEntity containing the refreshed AccessTokenResponse.
     */
    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<AccessTokenResponse> refreshToken(
            @RequestBody Map<String, ?> requestParams,
            @PathVariable String realm
    );
}
