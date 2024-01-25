package io.ylab.gatewayreactiveservice.config;

import io.ylab.gatewayreactiveservice.config.utils.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

/**
 * Security configuration class responsible for configuring web security.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    /**
     * Configures the security settings for the WebFlux application.
     *
     * @param http The {@link ServerHttpSecurity} to configure security settings.
     * @return The configured {@link SecurityWebFilterChain}.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("Webflux security with auth activated");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/token/refresh").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .build();
    }

    /**
     * Configures the authentication entry point for unauthorized requests.
     *
     * @return The configured {@link ServerAuthenticationEntryPoint}.
     */
    private ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, exception) -> Mono.fromRunnable(() ->
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
    }

    /**
     * Configures the access denied handler for forbidden requests.
     *
     * @return The configured {@link ServerAccessDeniedHandler}.
     */
    private ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, exception) -> Mono.fromRunnable(() ->
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
    }
}
