package io.ylab.gatewayreactiveservice.config;

import io.ylab.gatewayreactiveservice.config.properties.PathProperties;
import io.ylab.gatewayreactiveservice.config.utils.UserIdFilterFunction;
import io.ylab.gatewayreactiveservice.config.utils.UserRoleFilterFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
@RequiredArgsConstructor
class RouteConfiguration {
    private final PathProperties pathProps;

    private final UserIdFilterFunction userIdFilterFunctions;
    private final UserRoleFilterFunction userRoleFilterFunctions;
    @Bean
    public RouterFunction<ServerResponse> instrumentRoute() {
        return route("bies_s")
                .route(
                        path(pathProps.getUsers(),pathProps.getAuth(), pathProps.getToken(), pathProps.getIdea()),
                        http(pathProps.getUserServiceDomain()))
                .before(stripPrefix(2))
                .filter(userIdFilterFunctions.addUserId("id"))
                .filter(userRoleFilterFunctions.addUserRole("role"))
                .build();
    }

//    @Bean
//    public UserIdFilterFunction userIdFilterFunctions() {
//        return new UserIdFilterFunction();
//    }
//    @Bean
//    public UserRoleFilterFunction userRoleFilterFunctions() {
//        return new UserRoleFilterFunction();
//    }
//    @Bean
//    public RouterFunction<ServerResponse> instrumentRoute() {
//        return route("bies_s")
//                .route(
//                        path(pathProps.getUsers(),pathProps.getAuth(), pathProps.getToken(), pathProps.getIdea()),
//                        http(pathProps.getGatewayDomainName()))
//                .before(stripPrefix(2))
//                .filter(userIdFilterFunctions().addUserId("id"))
//                .filter(userRoleFilterFunctions().addUserRole("role"))
//                .build();
//    }

}
