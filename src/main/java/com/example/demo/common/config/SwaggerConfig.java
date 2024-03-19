package com.example.demo.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "demo 서비스 API 명세서",
                description = "스프링부트 demo 서비스 CRUD 실습 API 명세서",
                version = "v1"),
    servers = {
                @Server(url = "http://localhost:9000", description = "local server"),
                @Server(url = "https://gridgetest-server.shop", description = "dev server")
    })
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/**", "/swagger-ui/**", "https://gridgetest-server.shop"};

        return GroupedOpenApi.builder()
                .group("demo 서비스 API v1")
                .pathsToMatch(paths)
                .build();
    }
}
