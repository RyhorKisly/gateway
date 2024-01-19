package io.ylab.gatewayreactiveservice;

import io.ylab.gatewayreactiveservice.config.properties.PathProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({PathProperties.class})
public class GatewayMvcServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayMvcServiceApplication.class, args);
	}

}
