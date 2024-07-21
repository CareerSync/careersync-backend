package com.example.demo.src.login;

import com.example.demo.common.Constant;
import com.example.demo.common.SessionService;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.exceptions.notfound.user.UserNotFoundException;
import com.example.demo.common.exceptions.unauthorized.user.UserAlreadyLoggedoutException;
import com.example.demo.common.exceptions.unauthorized.user.UserUnauthorizedException;
import com.example.demo.common.oauth.OAuthService;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.src.login.model.PostLoginReq;
import com.example.demo.src.login.model.PostLoginRes;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetSocialOAuthRes;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.SHA256;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Tag(name = "login 도메인", description = "로그인 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class LoginController {

    private final LoginService loginService;
    private final OAuthService oAuthService;
    private final SessionService sessionService;

    private static final String COOKIE_NAME = "access-token";

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<PostLoginRes>> login(@RequestBody PostLoginReq req, HttpServletRequest request, HttpServletResponse response) {

        String encryptedPW = new SHA256().encrypt(req.getPassword());
        User loginUser = loginService.login(req.getLoginId(), encryptedPW);
        if (loginUser == null) {
            return ResponseEntity.status(NOT_FOUND).body(ApiResponse.fail(NOT_FIND_USER, null));
        }

        HttpSession session = request.getSession(); // 세션이 존재하지 않을 시, 새로 생성
        session.setAttribute(LOGIN_MEMBER, loginUser.getId());

        addCookieToResponse(session, response);

        ApiResponse<PostLoginRes> apiResponse = ApiResponse.success(BaseResponseStatus.SUCCESS, new PostLoginRes(loginUser));
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/check-login")
    public ResponseEntity<ApiResponse<PostUserRes>> checkLogin(HttpServletRequest request) {
        HttpSession session = sessionService.getSessionFromCookie(request);

        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            ApiResponse<PostUserRes> apiResponse = ApiResponse.fail(BaseResponseStatus.UNAUTHORIZED_USER, null);
            return ResponseEntity.status(UNAUTHORIZED).body(apiResponse);
        }

        // Assuming you need to fetch user details using user ID stored in session
        UUID userId = (UUID) session.getAttribute(LOGIN_MEMBER);
        PostUserRes postUserRes = new PostUserRes(userId); // You might need to use a service to fetch user details if needed

        ApiResponse<PostUserRes> apiResponse = ApiResponse.success(BaseResponseStatus.SUCCESS, postUserRes);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = getSessionFromCookie(request);
        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            ApiResponse<Void> apiResponse = ApiResponse.fail(BaseResponseStatus.ALREADY_LOGGED_OUT_USER, null);
            return ResponseEntity.status(BAD_REQUEST).body(apiResponse);
        }

        session.invalidate();

        // 커스텀 쿠키 삭제
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .domain(".careersync.site")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .path("/")
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        ApiResponse<Void> apiResponse = ApiResponse.success(BaseResponseStatus.SUCCESS, null);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 타입에 따라 다른 종류의 소셜 로그인을 진행합니다. html 코드를 반환하므로, 브라우저에서 해당 주소로 접속하시길 바랍니다.")
    @GetMapping("/{socialLoginType}/login")
    public void socialLoginRedirect(@PathVariable(name = "socialLoginType") String socialLoginPath) throws IOException {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        oAuthService.accessRequest(socialLoginType);
    }

    @Operation(summary = "소셜 로그인 callback 처리", description = "인가 코드를 전달받고 설정한 리다이렉트 주소로 접속하였을 때 실행되는 API입니다.")
    @ResponseBody
    @GetMapping("/{socialLoginType}/login/callback")
    public ResponseEntity<ApiResponse<GetSocialOAuthRes>> socialLoginCallback(
            @PathVariable(name = "socialLoginType") String socialLoginPath,
            @RequestParam(name = "code") String code,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, BaseException {

        log.info(">> 소셜 로그인 API 서버로부터 받은 code : {}", code);
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        GetSocialOAuthRes getSocialOAuthRes = oAuthService.oAuthLoginOrJoin(socialLoginType, code);

        HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_MEMBER, getSocialOAuthRes.getId());

        addCookieToResponse(session, response);

        ApiResponse<GetSocialOAuthRes> apiResponse = ApiResponse.success(BaseResponseStatus.SUCCESS, getSocialOAuthRes);
        return ResponseEntity.ok(apiResponse);
    }

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

    private HttpSession getSessionFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    log.info("cookie의 sessionID: {}", sessionId);
                    // 세션에 해당하는 ID가 있을 경우에만 세션 반환, 아닐 경우엔 null
                    HttpSession session = request.getSession(false);

                    if (session != null && session.getId().equals(sessionId)) {
                        return session;
                    }
                }
            }
        }
        return null;
    }
}


