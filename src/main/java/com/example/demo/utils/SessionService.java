package com.example.demo.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.example.demo.common.Constant.LOGIN_MEMBER;
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private static final String COOKIE_NAME = "access-token";

    public static void addCookieToResponse(HttpSession session, HttpServletResponse response) {
        // 커스텀 쿠키 설정
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, session.getId())
                .domain(".careersync.site")
                .httpOnly(true)
                .secure(true)
                .maxAge(7 * 24 * 60 * 60) // 1 week
                .path("/")
                .sameSite("None")
                .build();

        log.info("Set-Cookie : {}", cookie);
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public HttpSession getSessionFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    HttpSession session = request.getSession(false);
                    log.info("cookie의 sessionID: {}", sessionId);
                    // 세션에 해당하는 ID가 있을 경우에만 세션 반환, 아닐 경우엔 null
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
