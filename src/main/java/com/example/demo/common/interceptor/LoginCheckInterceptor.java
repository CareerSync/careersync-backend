package com.example.demo.common.interceptor;

import com.example.demo.common.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.example.demo.common.Constant.LOGIN_MEMBER;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public LoginCheckInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = sessionService.getSessionFromCookie(request);

        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 되지 않은 유저입니다");
            return false;
        }

        return true;
    }
}