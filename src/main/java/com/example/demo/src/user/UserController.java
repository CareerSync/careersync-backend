package com.example.demo.src.user;


import com.example.demo.common.Constant;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.oauth.OAuthService;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Tag(name = "user 도메인", description = "회원가입 및 유저 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * [POST] /app/v1/users
     * @return ResponseEntity<ApiResponse<PostUserRes>>
     */
    // Body
    @Operation(summary = "회원가입", description = "입력된 회원 정보를 받아 회원을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "USER_CREATED"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "BAD_REQUEST"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
    })
    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PostUserRes>> createUser(@RequestBody @Valid PostUserReq postUserReq) {

        PostUserRes postUserRes = userService.createUser(postUserReq);
        return ResponseEntity.status(CREATED).body(ApiResponse.success(USER_CREATED, postUserRes));
    }


}
