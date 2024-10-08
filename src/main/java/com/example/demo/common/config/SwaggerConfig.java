package com.example.demo.common.config;

import com.example.demo.common.Constant;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.ArrayList;
import java.util.List;

import static com.example.demo.common.Constant.AUTH_TOKEN_HEADER;
import static org.springframework.boot.context.properties.bind.Bindable.listOf;

@OpenAPIDefinition(
        info = @Info(title = "CareerSync API 명세서",
                description = "Swagger 기반 API 명세서",
                version = "v1"),
    servers = {
                @Server(url = "http://localhost:9000", description = "local server")
                //@Server(url = "https://gridgetest-server.shop", description = "dev server")
    },
        security = {
                @SecurityRequirement(name = "X-ACCESS-TOKEN")
        }
    )
@RequiredArgsConstructor
@SecurityScheme(
        type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER,
        name = "X-ACCESS-TOKEN", description = "Auth Token"
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("demo 서비스 API v1")
                .pathsToMatch(paths)
                .build();
    }
}
