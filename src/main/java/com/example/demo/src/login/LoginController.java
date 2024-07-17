package com.example.demo.src.login;

import com.example.demo.common.Constant;
import com.example.demo.common.exceptions.notfound.user.UserNotFoundException;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.src.login.model.PostLoginReq;
import com.example.demo.src.login.model.PostLoginRes;
import com.example.demo.src.user.entity.User;
import com.example.demo.utils.SHA256;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.response.BaseResponseStatus.*;

@Slf4j
@Tag(name = "login 도메인", description = "로그인 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ApiResponse<PostLoginRes> login(@RequestBody PostLoginReq req, HttpServletRequest request, HttpServletResponse response){
        // Id, pw 입력받아서 loginService.login 로직 실행 -> 성공 : member 반환 , 실패 : null 반환

        String encryptedPW = new SHA256().encrypt(req.getPassword());

        User loginUser = loginService.login(req.getLoginId(), encryptedPW);
        // null 반환시 없는 유저입니다 오류 반환
        if(loginUser == null){
            throw new UserNotFoundException();
        }

        // 로그인 성공 로직
        HttpSession session = request.getSession();// 세션이 있으면 있는 세션 반환, 없으면 신규세션 생성
        session.setAttribute(LOGIN_MEMBER, loginUser);

        // HTTP 응답 헤더에 Set-Cookie 값 설정
        addCookieToResponse(session, response);

        return ApiResponse.success(SUCCESS, new PostLoginRes(loginUser));
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        //세션이 있으면 있는 세션 반환, 없으면 신규세션 생성 -> false : 없어도 신규 세션 생성 x , default = true
        HttpSession session = request.getSession(false);//
        if(session != null){
            session.invalidate();
        }
        return "redirect:/board";
    }

    public static void addCookieToResponse(HttpSession session, HttpServletResponse response) {
        // Set session timeout to 1 week
        session.setMaxInactiveInterval(7 * 24 * 60 * 60);

        // Create a ResponseCookie with session ID
        ResponseCookie cookie = ResponseCookie.from("access_token", session.getId())
                .domain(".careersync.site")
                .httpOnly(true)
                .secure(true) // Only if you are using HTTPS
                .maxAge(7 * 24 * 60 * 60) // 1 week in seconds
                .path("/")
                .sameSite("None")
                .build();

        // Add the cookie to the response
        response.addHeader("Set-Cookie", cookie.toString());
    }


}
