package io.ylab.gatewayreactiveservice.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter responsible for extracting user ID from the JWT token and adding it as a header to the request.
 */
@Component
@RequiredArgsConstructor
public class UserIdFilter implements GlobalFilter {

    private final ReactiveJwtDecoder jwtDecoder;

    /**
     * Filters requests to extract the user ID from the JWT token and adds it as a header to the request.
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
                .map(jwt -> jwt.getClaim("sub"))
                .map(sub -> exchange.getRequest().mutate().header("User-Id", sub.toString()).build())
                .flatMap(x -> chain.filter(exchange));
    }
}
