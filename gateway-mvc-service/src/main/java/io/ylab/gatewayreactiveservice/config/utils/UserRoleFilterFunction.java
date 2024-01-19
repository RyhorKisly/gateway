package io.ylab.gatewayreactiveservice.config.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.*;

@Component
public class UserRoleFilterFunction {
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    public HandlerFilterFunction<ServerResponse, ServerResponse> addUserRole(String requestHeader) {
        return (request, next) -> {
            Optional<String> role = extractRole();
            if(role.isPresent()) {
                ServerRequest modified = ServerRequest.from(request).header(requestHeader, role.get()).build();
                return next.handle(modified);
            } else {
                return next.handle(request);
            }
        };
    }

    private Optional<String> extractRole() {
        Collection<String> roles = new ArrayList<>();
        Jwt jwt = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            jwt = ((JwtAuthenticationToken) authentication).getToken();
        }
        if(jwt != null) {
            roles = extractRoles(jwt);
        }
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.iterator().next());
    }

    private Collection<String> extractRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.get(resourceId) != null) {
            Map<String, Object> account = (Map<String, Object>) resourceAccess.get(resourceId);
            if (account.containsKey("roles")) {
                return  (Collection<String>) account.get("roles");
            }
        }
        return Collections.emptyList();
    }
}
