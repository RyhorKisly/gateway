package io.ylab.gatewayreactiveservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "path")
@Getter
@Setter
public class PathProperties {
    private String users;
    private String auth;
    private String token;
    private String idea;
    private String userServiceDomain;
    private String ideaServiceDomain;

}
