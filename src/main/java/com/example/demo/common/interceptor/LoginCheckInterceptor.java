package com.example.demo.common.interceptor;

import com.example.demo.utils.SessionService;
import com.example.demo.common.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.example.demo.common.Constant.LOGIN_MEMBER;
import static com.example.demo.common.response.BaseResponseStatus.UNAUTHORIZED_USER;

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
            throw new BaseException(UNAUTHORIZED_USER);
        }

        return true;
    }
}