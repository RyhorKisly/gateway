package io.ylab.gatewayreactiveservice.config.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Optional;

@Component
public class UserIdFilterFunction {

    public HandlerFilterFunction<ServerResponse, ServerResponse> addUserId(String requestHeader) {
        return (request, next) -> {
            Optional<String> userId = getUserIdFromToken();
            if(userId.isPresent()) {
                ServerRequest modified = ServerRequest.from(request).header(requestHeader, userId.get()).build();
                return next.handle(modified);
            } else {
                return next.handle(request);
            }
        };
    }

    public Optional<String> getUserIdFromToken() {
        Jwt jwt = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            jwt = ((JwtAuthenticationToken) authentication).getToken();
        }
        if(jwt == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(jwt.getClaim("sub"));
        }
    }
}
