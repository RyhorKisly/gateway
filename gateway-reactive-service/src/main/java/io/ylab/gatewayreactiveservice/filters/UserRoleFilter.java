package io.ylab.gatewayreactiveservice.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * Global filter responsible for extracting user roles from the JWT token and adding them as a header to the request.
 */
@Component
@RequiredArgsConstructor
public class UserRoleFilter implements GlobalFilter {
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;
    private final ReactiveJwtDecoder jwtDecoder;

    /**
     * Filters requests to extract user roles from the JWT token and adds them as a header to the request.
     *
     * @param exchange The {@link ServerWebExchange} representing the current exchange.
     * @param chain    The {@link GatewayFilterChain} for chaining multiple filters.
     * @return A {@link Mono} representing the completion of the filter execution.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        final String token = header.split(" ")[1].trim();

        return jwtDecoder.decode(token)
                .flatMap(jwt -> extractRole(jwt)
                                .map(role -> exchange.getRequest().mutate().header("User-Role", role).build())
                                .map(request -> chain.filter(exchange.mutate().request(request).build()))
                                .orElseGet(() -> chain.filter(exchange)));
    }


    /**
     * Extracts user role from the provided JWT token.
     *
     * @param jwt The {@link Jwt} token from which roles are extracted.
     * @return An {@link Optional} containing the extracted role, or empty if not found.
     */
    private Optional<String> extractRole(Jwt jwt) {
        return Optional.ofNullable(jwt.getClaimAsMap("resource_access"))
                .map(resourceAccess -> (Map<String, List<String>>) resourceAccess.get(resourceId))
                .map(clientAccess -> clientAccess.get("roles"))
                .flatMap(roles -> roles.stream().findFirst());
    }
}
