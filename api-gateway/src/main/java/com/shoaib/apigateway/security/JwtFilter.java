package com.shoaib.apigateway.security;

import com.shoaib.redis.RedisAuthHelperMethods;
import com.shoaib.security.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RedisAuthHelperMethods  redisAuthHelperMethods;
    private final ObjectMapper  objectMapper;

    private static final List<String> PUBLIC_URLS = List.of(
            "/api/v1/auth",
            "/api/v1/public"
    );

    public JwtFilter(JwtUtil jwtUtil, RedisAuthHelperMethods redisAuthHelperMethods, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.redisAuthHelperMethods = redisAuthHelperMethods;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        boolean isPublic = PUBLIC_URLS.stream()
                .anyMatch(path::startsWith);

        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {

            if(redisAuthHelperMethods.isTokenBlacklisted(token)){
                return unauthorized(
                        exchange,
                        "Token revoked. Please login again."
                );
            }

            if (!jwtUtil.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            UUID userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Id", userId.toString())
                            .header("X-User-Role", role))
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange,
            String message) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        try {

            byte[] body = objectMapper.writeValueAsBytes(
                    Map.of(
                            "success", false,
                            "message", message
                    )
            );

            DataBuffer buffer =
                    exchange.getResponse()
                            .bufferFactory()
                            .wrap(body);

            return exchange.getResponse()
                    .writeWith(Mono.just(buffer));

        } catch (Exception e) {

            return exchange.getResponse()
                    .setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}