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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "유저 회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                {
                    "apiVersion": "1.0.0",
                    "timestamp": "2023-07-01T12:34:56Z",
                    "status": "USER_CREATED",
                    "statusCode": 201,
                    "message": "유저 생성이 완료되었습니다",
                    "data": {
                        "userId": 1
                    }
                }
                """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                        유저 회원가입 요청 데이터 중 오류가 발생할 수 있는 경우:
                        1. 유저 회원가입 시, 중복되는 userId인 경우 오류 발생
                        2. 유저 회원가입 요청 데이터 중, null이거나 길이에 맞지 않는 값이 있는 경우 오류 발생
                    """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "Duplicate userId", value = """
                                            {
                                               "apiVersion": "1.0.0",
                                               "timestamp": "2024-07-21T23:41:01+09:00",
                                               "status": "fail",
                                               "statusCode": 400,
                                               "message": "이미 존재하는 유저 아이디입니다."
                                             }
                """), @ExampleObject(name = "Validation Errors", value = """
                                           {
                                              "apiVersion": "1.0.0",
                                              "timestamp": "2024-07-21T23:40:05+09:00",
                                              "status": "fail",
                                              "statusCode": 400,
                                              "message": "INVALID_REQUEST",
                                              "errors": [
                                                {
                                                  "field": "userId",
                                                  "errorCode": "REQUIRED_FIELD",
                                                  "message": "유저 아이디는 null 혹은 빈 문자열 일 수 없습니다."
                                                },
                                                {
                                                  "field": "userName",
                                                  "errorCode": "INVALID_SIZE",
                                                  "message": "유저 이름은 10자 이내여야 합니다."
                                                }
                                              ]
                                            }
                """)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                       "apiVersion": "1.0.0",
                                       "timestamp": "2024-07-21T23:41:23+09:00",
                                       "status": "error",
                                       "statusCode": 500,
                                       "message": "예상치 못한 에러가 발생했습니다."
                                     }
                """)
                    )
            )
    })
    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PostUserRes>> createUser(@RequestBody @Valid PostUserReq postUserReq) {

        PostUserRes postUserRes = userService.createUser(postUserReq);
        return ResponseEntity.status(CREATED).body(ApiResponse.success(USER_CREATED, postUserRes));
    }


}
