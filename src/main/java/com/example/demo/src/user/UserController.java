package com.example.demo.src.user;


import com.example.demo.common.Constant;
import com.example.demo.common.oauth.OAuthService;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.response.BaseResponseStatus.POST_USERS_INVALID_EMAIL;
import static com.example.demo.common.response.BaseResponseStatus.USERS_EMPTY_EMAIL;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@Slf4j
@Tag(name = "user 도메인", description = "회원가입 및 유저 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @Operation(summary = "회원가입", description = "입력된 회원 정보를 받아 회원을 생성합니다. oauth가 true면 소셜 로그인을 하라고 안내합니다.")
    @ResponseBody
    @PostMapping("")
    public ApiResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
//        if(postUserReq.getUserId() == null){
//            return new BaseResponse<>(USERS_EMPTY_EMAIL.isSuccess(),
//                    "유저 이메일이 비어있습니다",
//                    USERS_EMPTY_EMAIL.getCode());
//        }

        PostUserRes postUserRes = userService.createUser(postUserReq);
        return ApiResponse.success(postUserRes);
    }


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



}
