package com.ecom.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter(5,10,1);
    }

    @Bean
    public KeyResolver hostKeyResolver(){
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getHostName()
        );
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service",
                        r -> r.path("/api/users/**")
                                .filters(f -> f.circuitBreaker(config -> config.setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/users")))
                                .uri("lb://USER-SERVICE"))
                .route("product-service",
                        r -> r.path("/api/products/**")
                                .filters(f -> f.circuitBreaker(config -> config.setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/products"))
                                        .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                                .setKeyResolver(hostKeyResolver())))
                                .uri("lb://PRODUCT-SERVICE"))
                .route("order-service",
                        r -> r.path("/api/cart/**", "/api/orders/**")
                                .uri("lb://ORDER-SERVICE"))
                .route("eureka",
                        r -> r.path("/eureka/main")
                                .filters(f -> f.rewritePath("/eureka/main","/"))
                                .uri("http://localhost:8989"))
                .route("eureka-static-server",
                        r -> r.path("/eureka/**")
                                .uri("http://localhost:8989")).build();
    }
}
