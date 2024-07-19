package com.example.demo.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = {"/app/auth/login", "/app/auth/google/login/callback"})
public class CookieFilter implements Filter {

        private static final String COOKIE_NAME = "access-token";
        private static final String DOMAIN_NAME = ".careersync.site";

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // Initialization logic if needed
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Continue with the request
            chain.doFilter(request, response);

            // Set the cookie after the request has been processed
            String requestURI = httpRequest.getRequestURI();
            log.info("requestURI: {}", requestURI);

            if ("/app/auth/login".equals(requestURI) || "/app/auth/google/login/callback".equals(requestURI)) {
                // Obtain the session ID from the request
                String sessionId = httpRequest.getSession().getId();

                // Create and configure the cookie
                ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, sessionId)
                        .domain(DOMAIN_NAME)
                        .httpOnly(true)
                        .secure(true)
                        .maxAge(7 * 24 * 60 * 60) // 1 week
                        .path("/")
                        .sameSite("None")
                        .build();

                // Add the cookie to the response
                httpResponse.addHeader("Set-Cookie", cookie.toString());

                log.info("Set-Cookie: {}", cookie);
            }
        }

        @Override
        public void destroy() {
            // Cleanup logic if needed
        }

}