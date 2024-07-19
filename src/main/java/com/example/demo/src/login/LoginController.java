package com.example.demo.src.login;

import com.example.demo.common.Constant;
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
import com.example.demo.utils.SHA256;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.response.BaseResponseStatus.*;

@Slf4j
@Tag(name = "login 도메인", description = "로그인 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class LoginController {

    private final LoginService loginService;
    private final OAuthService oAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<PostLoginRes>> login(@RequestBody PostLoginReq req, HttpServletRequest request, HttpServletResponse response) {
        // Id, pw 입력받아서 loginService.login 로직 실행 -> 성공 : member 반환 , 실패 : null 반환

        // 유저가 입력한 비밀번호 암호화 -> DB에 저장된 유저 정보 조회하기 위해서
        String encryptedPW = new SHA256().encrypt(req.getPassword());

        User loginUser = loginService.login(req.getLoginId(), encryptedPW);
        // null 반환시 없는 유저입니다 오류 반환
        if (loginUser == null) {
            ApiResponse<PostLoginRes> apiResponse = ApiResponse.fail(NOT_FIND_USER, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        // 로그인 성공 로직
        HttpSession session = request.getSession(); // 세션이 있으면 있는 세션 반환, 없으면 신규세션 생성
        session.setAttribute(LOGIN_MEMBER, loginUser);

        // HTTP 응답 헤더에 Set-Cookie 값 설정
        addCookieToResponse(session, response);

        ApiResponse<PostLoginRes> apiResponse = ApiResponse.success(BaseResponseStatus.SUCCESS, new PostLoginRes(loginUser));
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/check-login")
    public ResponseEntity<ApiResponse<PostLoginRes>> checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            ApiResponse<PostLoginRes> apiResponse = ApiResponse.fail(BaseResponseStatus.UNAUTHORIZED_USER, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        User loginUser = (User) session.getAttribute(LOGIN_MEMBER);
        ApiResponse<PostLoginRes> apiResponse = ApiResponse.success(BaseResponseStatus.SUCCESS, new PostLoginRes(loginUser));
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            ApiResponse<Void> apiResponse = ApiResponse.fail(BaseResponseStatus.ALREADY_LOGGED_OUT_USER, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        // 세션 무효화
        session.invalidate();

        // 쿠키 만료 설정
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .domain(".careersync.site")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .path("/")
                .sameSite("None")
                .build();

        // HTTP 응답헤더에 쿠키 정보 추가
        response.addHeader("Set-Cookie", cookie.toString());

        ApiResponse<Void> apiResponse = ApiResponse.success(BaseResponseStatus.SUCCESS, null);
        return ResponseEntity.ok(apiResponse);
    }


    public static void addCookieToResponse(HttpSession session, HttpServletResponse response) {
        // 세션 만료 시간 1주일로 설정
        session.setMaxInactiveInterval(7 * 24 * 60 * 60);

        // 세션ID를 기반으로 Cookie 생성
        ResponseCookie cookie = ResponseCookie.from("access_token", session.getId())
                .domain(".careersync.site")
                .httpOnly(true)
                .secure(true) // Only if you are using HTTPS
                .maxAge(7 * 24 * 60 * 60) // 1 week in seconds
                .path("/")
                .sameSite("None")
                .build();

        // HTTP 응답헤더에 쿠키 정보 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }
    /**
     * 유저 소셜 가입, 로그인 인증으로 리다이렉트 해주는 url
     * [GET] /app/users/auth/:socialLoginType/login
     * @return void
     */
    @Operation(summary = "소셜 로그인", description = "소셜 로그인 타입에 따라 다른 종류의 소셜 로그인을 진행합니다. html 코드를 반환하므로, 브라우저에서 해당 주소로 접속하시길 바랍니다.")
    @GetMapping("/auth/{socialLoginType}/login")
    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String SocialLoginPath) throws IOException {
        SocialLoginType socialLoginType= SocialLoginType.valueOf(SocialLoginPath.toUpperCase());
        oAuthService.accessRequest(socialLoginType);
    }


    /**
     * Social Login API Server 요청에 의한 callback 을 처리
     * @param socialLoginPath (GOOGLE, FACEBOOK, NAVER, KAKAO)
     * @param code API Server 로부터 넘어오는 code
     * @return SNS Login 요청 결과로 받은 Json 형태의 java 객체 (access_token, jwt_token, user_num 등)
     */
    @Operation(summary = "소셜 로그인 callback 처리", description = "인가 코드를 전달받고 설정한 리다이렉트 주소로 접속하였을 때 실행되는 API입니다.")
    @ResponseBody
    @GetMapping(value = "/auth/{socialLoginType}/login/callback")
    public BaseResponse<GetSocialOAuthRes> socialLoginCallback(
            @PathVariable(name = "socialLoginType") String socialLoginPath,
            @RequestParam(name = "code") String code) throws IOException, BaseException {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code : {}", code);
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        GetSocialOAuthRes getSocialOAuthRes = oAuthService.oAuthLoginOrJoin(socialLoginType, code);
        return new BaseResponse<>(getSocialOAuthRes);
    }

}
