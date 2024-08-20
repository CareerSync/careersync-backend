package com.example.demo.src.user;


import com.example.demo.common.Constant;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.oauth.OAuthService;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.common.response.common.CommonApiResponse;
import com.example.demo.common.response.user.UserApiResponse;
import com.example.demo.src.user.model.PatchUserInfoReq;
import com.example.demo.src.user.model.PatchUserRes;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.response.ApiResponse.*;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Tag(name = "user 도메인", description = "회원가입 및 유저 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    /**
     * 회원가입 API
     * [POST] /v1/users/register
     * @return ResponseEntity<ApiResponse<PostUserRes>>
     */
    @Operation(summary = "회원가입", description = "입력된 회원 정보를 받아 회원을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "유저 회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = UserApiResponse.USER_CREATED)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유저 회원가입 요청 데이터 중 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "Duplicate userId", value = UserApiResponse.USER_ID_EXIST),
                                    @ExampleObject(name = "Duplicate userName", value = UserApiResponse.USER_NAME_EXIST),
                                    @ExampleObject(name = "Validation Errors", value = UserApiResponse.USER_INVALID_REQUEST)
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
    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PostUserRes>> createUser(@RequestBody @Valid PostUserReq postUserReq) {

        PostUserRes postUserRes = userService.createUser(postUserReq);
        return ResponseEntity.status(CREATED).body(success(USER_CREATED, postUserRes));
    }

    /**
     * 유저 기술스택, 경력, 최종학력 입력 API
     * [PATCH] /v1/users/info
     * @return ResponseEntity<ApiResponse<PatchUserRes>>
     */
    @Operation(summary = "유저 추가 정보 입력 API", description = "유저 기술스택, 경력, 최종학력 정보를 받아 유저 정보를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 추가 정보 입력 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = UserApiResponse.USER_INFO_MODIFIED)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                        유저 추가 정보 요청 데이터 중 오류가 발생할 수 있는 경우:
                        1. 유저 기술스택 배열이 비어있거나 빈 문자열로 이뤄져 있는 경우 오류 발생
                        2. 유저 경력 숫자가 0미만일 경우 오류 발생
                        3. 유저 최종학력이 null이거나 빈 문자열일 경우 오류 발생
                    """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "Empty Value in TechStacks", value = UserApiResponse.INVALID_USER_TECH_STACKS),
                                    @ExampleObject(name = "Wrong career value", value = UserApiResponse.INVALID_USER_CAREER),
                                    @ExampleObject(name = "Empty or null education value", value = UserApiResponse.INVALID_USER_EDUCATION)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 하지 않은 유저일때 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "추가 정보 입력할 유저가 존재하지 않을 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = UserApiResponse.NOT_FIND_USER)
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
    @PatchMapping("/info")
    public ResponseEntity<ApiResponse<PatchUserRes>> modifyUserInfo (HttpServletRequest request, @RequestBody @Valid PatchUserInfoReq patchUserInfoReq) throws JsonProcessingException {

        UUID id = (UUID) sessionService.getUserIdFromSession(request);
        PatchUserRes patchUserRes = userService.modifyUserInfo(id, patchUserInfoReq);
        return ResponseEntity.status(OK).body(success(SUCCESS, patchUserRes));
    }

    /**
     * @Deprecated
     * 유저 기술스택, 경력, 최종학력 입력 API
     * [PATCH] /v1/users/{id}/info
     * @return ResponseEntity<ApiResponse<PatchUserRes>>
     */
    @Deprecated
    @Operation(summary = "유저 추가 정보 입력 API", description = "유저 기술스택, 경력, 최종학력 정보를 받아 유저 정보를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "유저 추가 정보 입력 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                {
                    "apiVersion": "1.0.0",
                    "timestamp": "2023-07-01T12:34:56Z",
                    "status": "success",
                    "statusCode": 200,
                    "message": "요청에 성공하였습니다.",
                    "data": {
                        "id": 1
                    }
                }
                """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                        유저 추가 정보 요청 데이터 중 오류가 발생할 수 있는 경우:
                        1. 유저 기술스택 배열이 비어있거나 빈 문자열로 이뤄져 있는 경우 오류 발생
                        2. 유저 경력 숫자가 0미만일 경우 오류 발생
                        3. 유저 최종학력이 null이거나 빈 문자열일 경우 오류 발생
                    """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "Empty Value in TechStacks", value = """
                                            {
                                              "apiVersion": "1.0.0",
                                              "timestamp": "2024-07-30T17:16:23+09:00",
                                              "status": "fail",
                                              "statusCode": 400,
                                              "message": "INVALID_REQUEST",
                                              "errors": [
                                                {
                                                  "field": "techStacks[0]",
                                                  "errorCode": "REQUIRED_FIELD",
                                                  "message": "유저 기술스택은 null 혹은 빈 문자열 일 수 없습니다."
                                                }
                                              ]
                                            }
                """), @ExampleObject(name = "Validation Errors", value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-07-30T17:17:09+09:00",
                                      "status": "fail",
                                      "statusCode": 400,
                                      "message": "INVALID_REQUEST",
                                      "errors": [
                                        {
                                          "field": "education",
                                          "errorCode": "REQUIRED_FIELD",
                                          "message": "유저 최종학력은 null 혹은 빈 문자열 일 수 없습니다."
                                        },
                                        {
                                          "field": "career",
                                          "errorCode": "INVALID_VALUE",
                                          "message": "유저 경력 수치는 0 이상이어야 합니다."
                                        }
                                      ]
                                    } ]
                                            }
                """)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "추가 정보 입력할 유저가 존재하지 않을 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-07-30T17:17:46+09:00",
                                      "status": "fail",
                                      "statusCode": 404,
                                      "message": "일치하는 유저가 없습니다."
                                    }
                """)
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
                    "timestamp": "2023-07-01T12:34:56Z",
                    "status": "fail",
                    "statusCode": 500,
                    "message": "예상치 못한 에러가 발생했습니다."
                }
                """)
                    )
            )
    })
    @ResponseBody
    @PatchMapping("/{id}/info")
    public ResponseEntity<ApiResponse<PatchUserRes>> modifyUserInfoWithId(@PathVariable(name = "id") UUID id,
                                                                       @RequestBody @Valid PatchUserInfoReq patchUserInfoReq) throws JsonProcessingException {

        PatchUserRes patchUserRes = userService.modifyUserInfo(id, patchUserInfoReq);
        return ResponseEntity.status(OK).body(success(SUCCESS, patchUserRes));
    }


}
