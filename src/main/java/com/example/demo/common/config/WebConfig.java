package com.example.demo.common.config;


import com.example.demo.common.interceptor.CookieInterceptor;
import com.example.demo.common.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;

    @Autowired
    public WebConfig(LoginCheckInterceptor loginCheckInterceptor) {
        this.loginCheckInterceptor = loginCheckInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 앞으로 만들 모든 CORS 정보를 적용한다
        registry.addMapping("/**")
                .allowedOrigins("https://local.careersync.site", "https://careersync.site")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/app/v1/auth/login",
                        "/app/v1/auth/google/login",
                        "/app/v1/auth/google/login/callback",
                        "/app/v1/users/register",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api-docs/**"
                );
    }
}