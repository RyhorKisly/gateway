package io.ylab.gatewayreactiveservice.filters;

import io.ylab.gatewayreactiveservice.config.properties.AppProperties;
import io.ylab.gatewayreactiveservice.core.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter responsible for extracting user ID from the SecurityContext and adding it as a header to the request.
 */
@Component
@RequiredArgsConstructor
public class UserIdFilter implements GlobalFilter {
    private final AppProperties properties;

    /**
     * Filters requests to extract the user ID from the JWT token and adds it as a header to the request.
     *
     * @param exchange The {@link ServerWebExchange} representing the current exchange.
     * @param chain    The {@link GatewayFilterChain} for chaining multiple filters.
     * @return A {@link Mono} representing the completion of the filter execution.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getDetails)
                .switchIfEmpty(chain.filter(exchange))
                .cast(UserDTO.class)
                .map(dto -> exchange.getRequest().mutate().header(properties.getIdHeader(), dto.id()).build())
                .flatMap(request -> chain.filter(exchange));
    }
}
