package com.hse.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("file_storing_service", r -> r
                        .path("/api/v1/files/**")
                        .uri("http://localhost:8081"))
                .route("file_analysis_service", r -> r
                        .path("/api/v1/analysis/**")
                        .uri("http://localhost:8082"))
                .build();
    }
}
