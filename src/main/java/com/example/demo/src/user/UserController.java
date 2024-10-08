package com.example.demo.src.user;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "user 도메인", description = "회원 API, 소셜 로그인 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/users")
public class UserController {

//    private final UserService userService;
//
//    /**
//     * 로그인 API
//     * [POST] /app/users/logIn
//     * @return BaseResponse<PostLoginRes>
//     */
//    @Operation(summary = "회원 로그인", description = "입력된 회원 이메일과 비밀번호에 해당하는 jwt 토큰 값을 반환받습니다.")
//    @ResponseBody
//    @PostMapping("/logIn")
//    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
//        // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
//        // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
//        PostLoginRes postLoginRes = userService.logIn(postLoginReq);
//        return new BaseResponse<>(postLoginRes, messageUtils.getMessage("SUCCESS"));
//    }
//
//
//    /**
//     * 유저 소셜 가입, 로그인 인증으로 리다이렉트 해주는 url
//     * [GET] /app/users/auth/:socialLoginType/login
//     * @return void
//     */
//    @Operation(summary = "소셜 로그인", description = "소셜 로그인 타입에 따라 다른 종류의 소셜 로그인을 진행합니다. html 코드를 반환하므로, 브라우저에서 해당 주소로 접속하시길 바랍니다.")
//    @GetMapping("/auth/{socialLoginType}/login")
//    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String SocialLoginPath) throws IOException {
//        SocialLoginType socialLoginType= SocialLoginType.valueOf(SocialLoginPath.toUpperCase());
//        oAuthService.accessRequest(socialLoginType);
//    }
//
//
//    /**
//     * Social Login API Server 요청에 의한 callback 을 처리
//     * @param socialLoginPath (GOOGLE, FACEBOOK, NAVER, KAKAO)
//     * @param code API Server 로부터 넘어오는 code
//     * @return SNS Login 요청 결과로 받은 Json 형태의 java 객체 (access_token, jwt_token, user_num 등)
//     */
//    @Operation(summary = "소셜 로그인 callback 처리", description = "인가 코드를 전달받고 설정한 리다이렉트 주소로 접속하였을 때 실행되는 API입니다.")
//    @ResponseBody
//    @GetMapping(value = "/auth/{socialLoginType}/login/callback")
//    public BaseResponse<GetSocialOAuthRes> socialLoginCallback(
//            @PathVariable(name = "socialLoginType") String socialLoginPath,
//            @RequestParam(name = "code") String code) throws IOException, BaseException{
//        log.info(">> 소셜 로그인 API 서버로부터 받은 code : {}", code);
//        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
//        GetSocialOAuthRes getSocialOAuthRes = oAuthService.oAuthLoginOrJoin(socialLoginType, code);
//        return new BaseResponse<>(getSocialOAuthRes, messageUtils.getMessage("SUCCESS"));
//    }


}
