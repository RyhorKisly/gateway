package io.ylab.gatewayreactiveservice.config.utils;

import io.ylab.gatewayreactiveservice.core.dto.UserDTO;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtAuthConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;
    private final Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new ReactiveJwtGrantedAuthoritiesConverterAdapter(
            new JwtGrantedAuthoritiesConverter());
    private final String principalClaimName = JwtClaimNames.SUB;

    @Override
    public Mono<AbstractAuthenticationToken> convert(@Nonnull Jwt jwt) {
        return Objects.requireNonNull(jwtGrantedAuthoritiesConverter.convert(jwt))
                .collectList()
                .map((authorities) -> {
                    String principalName = jwt.getClaimAsString(this.principalClaimName);
                    UserDTO userDTO = generateUserDTO(jwt);
                    JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt, authorities, principalName);
                    jwtAuthToken.setDetails(userDTO);
                    return jwtAuthToken;
                });
    }

    private UserDTO generateUserDTO(Jwt jwt) {
        String userId = jwt.getClaim("sub");
        Optional<String> userRole = extractRole(jwt);
        String role = userRole.orElseThrow();
        return UserDTO.builder().id(userId).role(role).build();
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
