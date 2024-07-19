package com.example.demo.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.example.demo.common.Constant.LOGIN_MEMBER;
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private static final String COOKIE_NAME = "access-token";

    public HttpSession getSessionFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    HttpSession session = request.getSession(false);
                    if (session != null && session.getId().equals(sessionId)) {
                        return session;
                    }
                }
            }
        }
        return null;
    }

    public Object getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = getSessionFromCookie(request);
        return session != null ? session.getAttribute(LOGIN_MEMBER) : null;
    }
}
