package com.example.demo.src.chat;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.src.chat.model.PostAfterChatReq;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.chat.model.PostChatRes;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.PatchUserInfoReq;
import com.example.demo.src.user.model.PatchUserRes;
import com.example.demo.utils.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

import static com.example.demo.common.response.ApiResponse.success;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Tag(name = "chat 도메인", description = "대화 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/chats")
public class ChatController {

    private final ChatService chatService;
    private final SessionService sessionService;

    /**
     * 채팅 생성 후 첫 질문에 대한 답변받기 API
     * [POST] /v1/chats
     * @return ResponseEntity<ApiResponse<PostChatRes>>
     */
    @Operation(summary = "채팅 생성 후 첫 질문에 대한 답변받기 API", description = """
            채팅의 질문에 대한 답변을 반환한다.  
            
            대화 생성 후, `첫 질문인 경우엔 requestBody로 대화 식별자인 id값과 question을 함께 전송`해야 한다.  
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "대화 생성 및 질문에 대한 답변 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "일반 질문인 경우", value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-04T00:28:17+09:00",
                                      "status": "success",
                                      "statusCode": 201,
                                      "message": "대화 생성 및 질문에 대한 답변이 완료되었습니다",
                                      "data": {
                                        "title": "string",
                                        "answer": "sample_answer_from_fastapi_server_without_jobposts",
                                        "jobPosts": []
                                      }
                                    }
                
                """), @ExampleObject(name = "채용 공고 질문인 경우", value = """
                {
                      "apiVersion": "1.0.0",
                      "timestamp": "2024-08-04T00:25:15+09:00",
                      "status": "success",
                      "statusCode": 201,
                      "message": "대화 생성 및 질문에 대한 답변이 완료되었습니다",
                      "data": {
                        "title": "string",
                        "answer": "sample_answer_from_fastapi_server_with_jobposts",
                        "jobPosts": [
                          {
                            "id": "fde8ee04-45fd-4e7b-a0b2-b90be9caccf8",
                            "title": "jobPost_title",
                            "career": "신입",
                            "companyName": "jobPost_coname",
                            "endDate": "2025-01-01T00:00:00+09:00",
                            "techStacks": [
                              "python",
                              "java"
                            ],
                            "imgUrl": "http://image.com",
                            "siteUrl": "http://test.com"
                          },
                          {
                            "id": "4425d2ee-02b6-4d26-8442-b9fafa4bc77f",
                            "title": "jobPost_title",
                            "career": "신입",
                            "companyName": "jobPost_coname",
                            "endDate": "2025-01-01T00:00:00+09:00",
                            "techStacks": [
                              "python",
                              "java"
                            ],
                            "imgUrl": "http://image.com",
                            "siteUrl": "http://test.com"
                          }
                        ]
                      }
                    }
                """)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                    Invalid Request  
                    
                    1. 질문이 null 이거나 빈 문자열인 경우 에러 반환  
                    
                    2. 이미 존재하는 대화 식별자인 경우 에러 반환
                    
                    """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "필요한 request 값들이 null 이거나 빈 문자열인 경우", value = """
                                            {
                                              "apiVersion": "1.0.0",
                                              "timestamp": "2024-08-04T00:32:50+09:00",
                                              "status": "fail",
                                              "statusCode": 400,
                                              "message": "INVALID_REQUEST",
                                              "errors": [
                                                {
                                                  "field": "question",
                                                  "errorCode": "REQUIRED_FIELD",
                                                  "message": "질문은 null 혹은 빈 문자열 일 수 없습니다."
                                                }
                                              ]
                                            }
                """)
                            ,
                            @ExampleObject(name = "이미 존재하는 대화 식별자인 경우", value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-04T15:31:51+09:00",
                                      "status": "fail",
                                      "statusCode": 400,
                                      "message": "이미 존재하는 대화 식별자입니다."
                                    }
                """)
                    }

                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-04T00:23:43+09:00",
                                      "status": "fail",
                                      "statusCode": 401,
                                      "message": "로그인 된 사용자가 아닙니다."
                                    }
                """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "로그인 한 유저 정보가 데이터베이스에 없는 경우",
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
    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createChat (HttpServletRequest request, @RequestBody @Valid PostChatReq postChatReq) {

        UUID userId = (UUID) sessionService.getUserIdFromSession(request);
        UUID chatId = postChatReq.getId();

        PostChatRes chatRes = chatService.createChat(userId, chatId, postChatReq);
        return ResponseEntity.status(OK).body(success(CHAT_CREATED, chatRes));

    }

    /**
     * 추가 질문에 대한 답변받기 API
     * [POST] /v1/chats/:chatId
     * @return ResponseEntity<ApiResponse<PostChatRes>>
     */
    @Operation(summary = "대화의 추가 질문에 대한 답변받기 API", description = """
            생성된 대화의 첫 질문 이후, 추가 질문에 대한 답변을 받는다.
            
            `첫 질문 이후엔 pathVariable로 대화 식별자를, requestBody로 question을 전송`하면 된다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "채용 공고를 묻지 않는 일반 질문에 대한 답변 반환 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "일반 질문인 경우", value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-04T01:09:25+09:00",
                                      "status": "success",
                                      "statusCode": 201,
                                      "message": "추가 질문에 대한 답변이 완료되었습니다",
                                      "data": {
                                        "title": null,
                                        "answer": "sample_answer_from_fastapi_server_without_jobposts",
                                        "jobPosts": []
                                      }
                                    }
                """),@ExampleObject(name = "채용 공고 질문인 경우", value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-04T01:12:30+09:00",
                                      "status": "success",
                                      "statusCode": 201,
                                      "message": "추가 질문에 대한 답변이 완료되었습니다",
                                      "data": {
                                        "title": null,
                                        "answer": "sample_answer_from_fastapi_server_with_jobposts",
                                        "jobPosts": [
                                          {
                                            "id": "a6554dce-076f-470e-9af8-446fd33a8dcc",
                                            "title": "jobPost_title",
                                            "career": "신입",
                                            "companyName": "jobPost_coname",
                                            "endDate": "2025-01-01T00:00:00+09:00",
                                            "techStacks": [
                                              "python",
                                              "java"
                                            ],
                                            "imgUrl": "http://image.com",
                                            "siteUrl": "http://test.com"
                                          },
                                          {
                                            "id": "b6e1fed7-428a-4201-ad6b-2094a62d48d8",
                                            "title": "jobPost_title",
                                            "career": "신입",
                                            "companyName": "jobPost_coname",
                                            "endDate": "2025-01-01T00:00:00+09:00",
                                            "techStacks": [
                                              "python",
                                              "java"
                                            ],
                                            "imgUrl": "http://image.com",
                                            "siteUrl": "http://test.com"
                                          }
                                        ]
                                      }
                                    }
                """)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "질문이 null 이거나 빈 문자열인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "apiVersion": "1.0.0",
                                              "timestamp": "2024-08-04T00:32:50+09:00",
                                              "status": "fail",
                                              "statusCode": 400,
                                              "message": "INVALID_REQUEST",
                                              "errors": [
                                                {
                                                  "field": "question",
                                                  "errorCode": "REQUIRED_FIELD",
                                                  "message": "질문은 null 혹은 빈 문자열 일 수 없습니다."
                                                }
                                              ]
                                            }
                """)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-04T00:23:43+09:00",
                                      "status": "fail",
                                      "statusCode": 401,
                                      "message": "로그인 된 사용자가 아닙니다."
                                    }
                """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = """
                            Not Found  
                            
                            1. 로그인 한 유저 정보가 데이터베이스에 없는 경우  
                            
                            2. 요청한 대화 식별자에 해당하는 대화가 존재하지 않을 경우
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {@ExampleObject(name = "유저가 존재하지 않는 경우", value = """
                                                        {
                                                          "apiVersion": "1.0.0",
                                                          "timestamp": "2024-07-30T17:17:46+09:00",
                                                          "status": "fail",
                                                          "statusCode": 404,
                                                          "message": "일치하는 유저가 없습니다."
                                                        }
                                    """), @ExampleObject(name = "대화가 존재하지 않는 경우", value = """
                                                        {
                                                          "apiVersion": "1.0.0",
                                                          "timestamp": "2024-08-04T15:38:51+09:00",
                                                          "status": "fail",
                                                          "statusCode": 404,
                                                          "message": "일치하는 대화가 없습니다."
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
    @PostMapping("/{chatId}")
    public ResponseEntity<ApiResponse<?>> addAnswerToChat (HttpServletRequest request, @RequestParam(name = "chatId") UUID chatId,
                                                           @RequestBody @Valid PostAfterChatReq postAfterChatReq) {

        UUID userId = (UUID) sessionService.getUserIdFromSession(request);

        // 첫 질문이 아닌 경우
        PostChatRes afterChat = chatService.addAnswerToChat(userId, chatId, postAfterChatReq);
        return ResponseEntity.status(OK).body(success(ANSWER_CREATED, afterChat));

    }


}
