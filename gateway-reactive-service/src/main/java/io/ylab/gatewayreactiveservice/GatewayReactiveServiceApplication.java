package io.ylab.gatewayreactiveservice;

import io.ylab.gatewayreactiveservice.config.properties.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({AppProperties.class})
@SpringBootApplication
public class GatewayReactiveServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayReactiveServiceApplication.class, args);
	}

}
