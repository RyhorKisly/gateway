package io.ylab.gatewayreactiveservice.config;

import io.ylab.gatewayreactiveservice.config.utils.JwtAuthConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class responsible for configuring web security.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * The URI for retrieving JSON Web Keys (JWK) Set.
     */
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    /**
     * The converter responsible for extracting authentication details from JWT
     * and converting role to "ROLE_...".
     */
    private final JwtAuthConverter jwtAuthConverter;

    /**
     * Configures security filters and authorization rules.
     *
     * @param http The HTTP security object to configure security settings.
     * @return The configured security filter chain.
     * @throws Exception If an error occurs during security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                                .requestMatchers(HttpMethod.GET, "/api/v1/users").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/token/refresh").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling.authenticationEntryPoint((request, response, ex) ->
                                        response.setStatus(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        ))
                                .accessDeniedHandler((request, response, ex) ->
                                        response.setStatus(
                                                HttpServletResponse.SC_FORBIDDEN
                                        ))
                );
        return http.build();
    }
}
