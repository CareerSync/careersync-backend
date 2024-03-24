package com.example.demo.src.user;


import com.example.demo.common.Constant.SocialLoginType;
import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.oauth.OAuthService;
import com.example.demo.src.user.entity.User;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.user.model.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.data.history.Revisions;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


import static com.example.demo.common.entity.BaseEntity.*;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@Slf4j
@Tag(name = "user 도메인", description = "회원 API, 소셜 로그인 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/users")
public class UserController {


    private final UserService userService;

    private final OAuthService oAuthService;

    private final JwtService jwtService;

    private final MessageUtils messageUtils;


    /**
     * 회원가입 API
     * [POST] /app/users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @Operation(summary = "회원가입", description = "입력된 회원 정보를 받아 회원을 생성합니다. oauth가 true면 소셜 로그인을 하라고 안내합니다.")
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(USERS_EMPTY_EMAIL.isSuccess(),
                    messageUtils.getMessage("USERS_EMPTY_EMAIL"),
                    USERS_EMPTY_EMAIL.getCode());
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL.isSuccess(),
                    messageUtils.getMessage("POST_USERS_INVALID_EMAIL"),
                    POST_USERS_INVALID_EMAIL.getCode());
        }
        PostUserRes postUserRes = userService.createUser(postUserReq);
        return new BaseResponse<>(postUserRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /app/users? Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
    @Operation(summary = "회원 조회", description = "이메일 입력 시 해당되는 회원만 조회, 안할 시 모든 회원을 조회합니다.")
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        if(Email == null){
            List<GetUserRes> getUsersRes = userService.getUsers();
            return new BaseResponse<>(getUsersRes);
        }
        // Get Users
        List<GetUserRes> getUsersRes = userService.getUsersByEmail(Email);
        return new BaseResponse<>(getUsersRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 회원 1명 조회 API
     * [GET] /app/users/:userId
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @Operation(summary = "회원 1명 조회", description = "입력된 회원 id에 해당되는 유저를 조회합니다.")
    @ResponseBody
    @GetMapping("/{userId}") // (GET) 127.0.0.1:9000/app/users/:userId
    public BaseResponse<GetUserRes> getUser(@PathVariable("userId") Long userId) {
        GetUserRes getUserRes = userService.getUser(userId);
        return new BaseResponse<>(getUserRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 유저정보변경 API
     * [PATCH] /app/users
     * RequestBody: PatchUserReq
     * - name: 이름
     *
     * @return BaseResponse<String>
     */
    @Operation(summary = "회원 이름 수정", description = "입력된 이름을 가지고 기존 회원 이름을 수정합니다.")
    @ResponseBody
    @PatchMapping()
    public BaseResponse<String> modifyUserName(@RequestBody PatchUserReq patchUserReq){

        Long jwtUserId = jwtService.getUserId();
        log.info("jwtUserId: {}", jwtUserId);

        userService.modifyUserName(jwtUserId, patchUserReq);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_USER_SUCCESS"), messageUtils.getMessage("SUCCESS"));

    }

    /**
     * 유저 생일정보 변경 API
     * [PATCH] /app/users/birthDate
     * RequestBody: PatchUserBirthDateReq
     * - birthDate: 생일일자
     *
     * @return BaseResponse<String>
     */
    @Operation(summary = "회원 생일일자 수정", description = "입력된 생일일자를 가지고 기존 회원 생일일자를 수정합니다.")
    @ResponseBody
    @PatchMapping("/birthDate")
    public BaseResponse<String> modifyBirthDate(@RequestBody PatchUserBirthDateReq req){
        Long jwtUserId = jwtService.getUserId();
        userService.modifyBirthDate(jwtUserId, req);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_USER_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 유저 이용약관 수정 API
     * [PATCH] /app/users/privacyTerm
     * RequestBody: PatchUserPrivacyTermReq
     * - serviceTerm: 이용약관
     * - dataTerm: 데이터 정책
     * - locationTerm: 위치 기반 기능
     *
     * @return BaseResponse<String>
     */
    @Operation(summary = "회원 이용약관 수정", description = "입력된 이용약관 정보를 가지고 기존 회원 이용약관 정보를 수정합니다.")
    @ResponseBody
    @PatchMapping("/privacyTerm")
    public BaseResponse<String> modifyPrivacyTerm(@RequestBody PatchUserPrivacyTermReq req){
        Long jwtUserId = jwtService.getUserId();
        userService.modifyPrivacy(jwtUserId, req);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_USER_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 유저 상태 수정 API
     * [PATCH] /app/users/state
     * @return BaseResponse<String>
     */
    @Operation(summary = "회원 상태 수정", description = "입력된 상태값을 가지고 기존 회원 상태를 수정합니다.")
    @ResponseBody
    @PatchMapping("/state")
    public BaseResponse<String> modifyState(@RequestParam("state") String state){
        Long jwtUserId = jwtService.getUserId();
        if (!state.equals("ACTIVE") && !state.equals("INACTIVE")) {
            throw new BaseException(INVALID_STATE, messageUtils.getMessage("INVALID_STATE"));
        }

        userService.modifyState(jwtUserId, State.valueOf(state.toUpperCase()));
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_USER_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 유저정보삭제 API
     * [DELETE] /app/users/:userId
     * @return BaseResponse<String>
     */
    @Operation(summary = "회원 삭제", description = "jwt 토큰 내 회원 id에 해당하는 유저를 삭제합니다.")
    @ResponseBody
    @DeleteMapping()
    public BaseResponse<String> deleteUser(){
        Long jwtUserId = jwtService.getUserId();
        userService.deleteUser(jwtUserId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_USER_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 로그인 API
     * [POST] /app/users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @Operation(summary = "회원 로그인", description = "입력된 회원 이메일과 비밀번호에 해당하는 jwt 토큰 값을 반환받습니다.")
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
        // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
        PostLoginRes postLoginRes = userService.logIn(postLoginReq);
        return new BaseResponse<>(postLoginRes, messageUtils.getMessage("SUCCESS"));
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
            @RequestParam(name = "code") String code) throws IOException, BaseException{
        log.info(">> 소셜 로그인 API 서버로부터 받은 code : {}", code);
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        GetSocialOAuthRes getSocialOAuthRes = oAuthService.oAuthLoginOrJoin(socialLoginType, code);
        return new BaseResponse<>(getSocialOAuthRes, messageUtils.getMessage("SUCCESS"));
    }


}
