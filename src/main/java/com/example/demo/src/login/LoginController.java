package com.example.demo.src.login;

import com.example.demo.common.exceptions.notfound.user.AlreadyLoggedOutUserException;
import com.example.demo.common.response.common.CommonApiResponse;
import com.example.demo.common.response.login.LoginApiResponse;
import com.example.demo.src.chat.model.CheckLoginRes;
import com.example.demo.utils.RedisService;
import com.example.demo.utils.SessionService;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.oauth.OAuthService;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.src.login.model.PostLoginReq;
import com.example.demo.src.login.model.PostLoginRes;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetSocialOAuthRes;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.SHA256;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.io.IOException;
import java.util.UUID;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.response.ApiResponse.*;
import static com.example.demo.utils.SessionService.addCookieToResponse;
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
    private final RedisService redisService;

    private static final String COOKIE_NAME = "access-token";

    /**
     * 로그인 API
     * [POST] /v1/auth/login
     * @return ResponseEntity<ApiResponse<PostLoginRes>>
     */
    @Operation(summary = "로그인", description = """
            아이디와 비밀번호를 입력한 후, 로그인을 시도한다.
            로그인에 성공할 경우, 세션에 유저를 저장한 후 Response Header에 Set-Cookie값을 넣어서 전송한다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = LoginApiResponse.LOGIN_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유저 아이디 혹은 비밀번호가 빈 값인 경우, 혹은 비밀번호가 8자 미만일 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "LoginId Empty", value = LoginApiResponse.LOGIN_ID_EMPTY),
                                    @ExampleObject(name = "LoginPW Empty", value = LoginApiResponse.LOGIN_PW_EMPTY),
                                    @ExampleObject(name = "LoginPW too short", value = LoginApiResponse.LOGIN_PW_TOO_SHORT)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "입력한 아이디 혹은 비밀번호에 해당하는 유저가 없는 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = LoginApiResponse.NOT_FIND_USER)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<PostLoginRes>> login(@RequestBody @Valid PostLoginReq req, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        String encryptedPW = new SHA256().encrypt(req.getPassword());
        User loginUser = loginService.login(req.getUserId(), encryptedPW);

        HttpSession session = request.getSession(); // 세션이 존재하지 않을 시, 새로 생성
        session.setAttribute(LOGIN_MEMBER, loginUser.getId());

        addCookieToResponse(session, response);

        redisService.addUserTechStackToRedis(loginUser.getId());

        ApiResponse<PostLoginRes> apiResponse = success(BaseResponseStatus.SUCCESS, new PostLoginRes(loginUser));
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 로그인 체크 API
     * [POST] /v1/auth/check-login
     * @return ResponseEntity<ApiResponse<CheckLoginRes>>
     */
    @Operation(summary = "로그인 체크", description = """
            현재 유저가 로그인한 상태인지 확인한다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 되어 있는 유저의 식별자 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = LoginApiResponse.CHECK_LOGIN_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 유저가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @PostMapping("/check-login")
    public ResponseEntity<ApiResponse<CheckLoginRes>> checkLogin(HttpServletRequest request) {
        HttpSession session = sessionService.getSessionFromCookie(request);

        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            ApiResponse<CheckLoginRes> apiResponse = fail(BaseResponseStatus.UNAUTHORIZED_USER, null);
            return ResponseEntity.status(UNAUTHORIZED).body(apiResponse);
        }

        // Assuming you need to fetch user details using user ID stored in session
        UUID userId = (UUID) sessionService.getUserIdFromSession(request);

        CheckLoginRes checkLoginRes = loginService.checkIsLogin(userId);
        ApiResponse<CheckLoginRes> apiResponse = success(BaseResponseStatus.SUCCESS, checkLoginRes);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 로그아웃 API
     * [POST] /v1/auth/logout
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @Operation(summary = "로그아웃", description = """
            로그인 한 유저가 로그아웃을 시도한다.
            Request Header에 Cookie: access-token=123 형태로 들어가있는 상태여야한다.
            세션에서 유저를 삭제한 다음, 해당 유저의 쿠키 정보를 삭제한 상태로 Response Header에 넣어준다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = LoginApiResponse.LOGOUT_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 유저가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        HttpSession session = sessionService.getSessionFromCookie(request);
        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            throw new AlreadyLoggedOutUserException();
        }

        // Redis 데이터 삭제
        UUID userId = (UUID) sessionService.getUserIdFromSession(request);
        redisService.deleteUserChatAndTechStacks(userId);

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

        // 유저 기술 스택 업데이트
        redisService.addUserTechStackToRedis(userId);

        ApiResponse<Void> apiResponse = success(BaseResponseStatus.SUCCESS, null);
        return ResponseEntity.ok(apiResponse);
    }


    /**
     * 소셜 로그인 API
     * [POST] /v1/auth/{socialLoginType}/login
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @Operation(summary = "소셜 로그인", description = """
        소셜 로그인 타입에 따라 다른 종류의 소셜 로그인을 진행합니다. 
        html 코드를 반환하므로, 브라우저에서 해당 주소로 접속하시길 바랍니다.
        """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @GetMapping("/{socialLoginType}/login")
    public void socialLoginRedirect(@PathVariable(name = "socialLoginType") String socialLoginPath) throws IOException {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        oAuthService.accessRequest(socialLoginType);
    }

    /**
     * 소셜 로그인 callback 처리 API
     * [POST] /v1/auth/{socialLoginType}/login/callback
     * @return ResponseEntity<ApiResponse<GetSocialOAuthRes>>
     */
    @Operation(summary = "소셜 로그인 callback 처리", description = "인가 코드를 전달받고 설정한 리다이렉트 주소로 접속하였을 때 실행되는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 식별자와 구글 OAuth 서비스에서 제공한 Bearer Token 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = LoginApiResponse.SOCIAL_LOGIN_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.INTERNAL_SERVER_ERROR)
                    )
            )
    })
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

        redisService.addUserTechStackToRedis(getSocialOAuthRes.getId());

        ApiResponse<GetSocialOAuthRes> apiResponse = success(BaseResponseStatus.SUCCESS, getSocialOAuthRes);
        return ResponseEntity.ok(apiResponse);
    }
}


