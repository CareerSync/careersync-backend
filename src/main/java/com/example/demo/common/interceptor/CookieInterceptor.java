package com.example.demo.common.interceptor;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class CookieInterceptor implements HandlerInterceptor {

    private static final String COOKIE_NAME = "access-token";
    private static final String DOMAIN_NAME = ".careersync.site";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow the request to proceed
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        String requestURI = request.getRequestURI();

        // Check if the request URI matches the specific endpoints
        if ("/v1/auth/login".equals(requestURI) || "/v1/auth/google/login/callback".equals(requestURI)) {
            String sessionId = request.getSession().getId();
            ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, sessionId)
                    .domain(DOMAIN_NAME)
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(7 * 24 * 60 * 60) // 1 week
                    .path("/")
                    .sameSite("None")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // No need to handle anything here for now
    }

}
