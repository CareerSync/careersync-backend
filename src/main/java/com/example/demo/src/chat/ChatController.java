package com.example.demo.src.chat;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.src.chat.model.*;
import com.example.demo.src.jobpost.model.JobPostRes;
import com.example.demo.utils.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
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

    /**
     * 대화 전체 조회 API
     * [GET] /v1/chats
     * @return ResponseEntity<ApiResponse<GetChatsRes>>
     */
    @Operation(summary = "대화 전체 조회 API", description = """
            지금까지 생성된 대화 내역을 모두 조회한다.  
            
            대화가 수정된 날짜 기준으로 내림차순으로 정렬해서 가져온다.  
            
            대화가 마지막으로 수정된 날짜 기준으로 정렬되도록 설정하였기에, `/v1/chats?page=0&size=1` 와 같이, page와 size만 query string으로 지정해주면 된다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = """
                            대화 내역 전체 조회 성공  
                            
                            각 대화에서 `추천받은 채용공고 갯수`도 함께 반환된다.
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = """
                    {
                                                  "apiVersion": "1.0.0",
                                                  "timestamp": "2024-08-05T20:56:35+09:00",
                                                  "status": "success",
                                                  "statusCode": 200,
                                                  "message": "요청에 성공하였습니다.",
                                                  "data": {
                                                    "list": [
                                                      {
                                                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa5",
                                                        "title": "string",
                                                        "updatedAt": "2024-08-05T20:56:29",
                                                        "recJobPostNum": 2
                                                      },
                                                      {
                                                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                        "title": "string",
                                                        "updatedAt": "2024-08-05T20:54:19",
                                                        "recJobPostNum": 4
                                                      }
                                                    ],
                                                    "page": 0,
                                                    "size": 2
                                                  }
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
    @GetMapping("")
    public ResponseEntity<ApiResponse<GetChatsRes>> getChats (HttpServletRequest request,
                                                              @PageableDefault(size = 12, sort = "updatedAt",  direction = Sort.Direction.DESC) Pageable pageable) {

        UUID userId = (UUID) sessionService.getUserIdFromSession(request);
        GetChatsRes chats = chatService.getChats(userId, pageable);
        return ResponseEntity.status(OK).body(success(SUCCESS, chats));

    }

    /**
     * 대화 단건 조회 API
     * [GET] /v1/chats/:chatId
     * @return ResponseEntity<ApiResponse<GetChatRes>>
     */
    @Operation(summary = "대화 단건 조회 API", description = """
            하나의 대화 내역을 조회한다.  
            
            각 대화에서 생성된 답변과 질문이 생성일자 기준으로 내림차순 정렬되어 반환된다.  
            
            답변에 채용공고 정보가 있다면 포함해서 반환된다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = """
                            대화 내역 단건 조회 성공
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                               "apiVersion": "1.0.0",
                                               "timestamp": "2024-08-06T22:17:17+09:00",
                                               "status": "success",
                                               "statusCode": 200,
                                               "message": "요청에 성공하였습니다.",
                                               "data": {
                                                 "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                 "title": "string",
                                                 "list": [
                                                   {
                                                     "id": "8949f309-9868-4548-80ce-388b471eda85",
                                                     "text": "sample_answer_from_fastapi_server_with_jobposts",
                                                     "createdAt": "2024-08-06T22:16:59",
                                                     "type": "answer",
                                                     "jobPosts": [
                                                       {
                                                         "id": "d789fc0e-1065-4101-b99b-1033196cad3d",
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
                                                         "id": "d7f7afbd-38db-4788-be65-044c2029450c",
                                                         "title": "jobPost_title",
                                                         "career": "신입",
                                                         "companyName": "jobPost_coname",
                                                         "endDate": "2025-01-01T00:00:00+09:00",
                                                         "techStacks": [
                                                           "java",
                                                           "python"
                                                         ],
                                                         "imgUrl": "http://image.com",
                                                         "siteUrl": "http://test.com"
                                                       }
                                                     ]
                                                   },
                                                   {
                                                     "id": "80ba8c07-f846-4a86-83fa-95788d0a529d",
                                                     "text": "string223",
                                                     "createdAt": "2024-08-06T22:16:59",
                                                     "type": "question",
                                                     "jobPosts": []
                                                   },
                                                   {
                                                     "id": "2abe3de2-3e4a-4a53-a8e2-37cf5dc2699c",
                                                     "text": "sample_answer_from_fastapi_server_with_jobposts",
                                                     "createdAt": "2024-08-06T22:16:55",
                                                     "type": "answer",
                                                     "jobPosts": [
                                                       {
                                                         "id": "38af20dd-1f43-4f09-b397-7a8894ecef47",
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
                                                         "id": "4c674bdd-2714-49de-8412-870a35a38a53",
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
                                                   },
                                                   {
                                                     "id": "515aa02f-502c-4623-b63e-04243f080230",
                                                     "text": "string22",
                                                     "createdAt": "2024-08-06T22:16:55",
                                                     "type": "question",
                                                     "jobPosts": []
                                                   },
                                                   {
                                                     "id": "0746cf21-8528-43cb-8f9b-cef357e94200",
                                                     "text": "sample_answer_from_fastapi_server_with_jobposts",
                                                     "createdAt": "2024-08-06T22:16:42",
                                                     "type": "answer",
                                                     "jobPosts": [
                                                       {
                                                         "id": "4ca15891-b002-4333-bf48-a0fcff7c5f13",
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
                                                         "id": "99d27edc-35b9-40bf-8ee1-5afe6bc75fc5",
                                                         "title": "jobPost_title",
                                                         "career": "신입",
                                                         "companyName": "jobPost_coname",
                                                         "endDate": "2025-01-01T00:00:00+09:00",
                                                         "techStacks": [
                                                           "java",
                                                           "python"
                                                         ],
                                                         "imgUrl": "http://image.com",
                                                         "siteUrl": "http://test.com"
                                                       }
                                                     ]
                                                   },
                                                   {
                                                     "id": "77e44c07-4f59-477b-8858-e9f396f89524",
                                                     "text": "string",
                                                     "createdAt": "2024-08-06T22:16:42",
                                                     "type": "question",
                                                     "jobPosts": []
                                                   }
                                                 ]
                                               }
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
                    description = "존재하지 않는 대화 내역인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-05T23:02:10+09:00",
                                      "status": "fail",
                                      "statusCode": 404,
                                      "message": "일치하는 대화가 없습니다."
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
    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<GetChatRes>> getChat (@PathVariable(name = "chatId") UUID chatId) {

        GetChatRes chatRes = chatService.getChat(chatId);
        return ResponseEntity.status(OK).body(success(SUCCESS, chatRes));

    }

    /**
     * 대화에서 추천된 채용공고 조회 API
     * [GET] /v1/chats/:chatId/jobposts
     * @return ResponseEntity<ApiResponse<List<JobPostRes>>>
     */
    @Operation(summary = "대화 내 추천 채용공고 조회 API", description = """
            각 대화에서 추천된 채용공고를 조회할 수 있다.  
            
            추천된 채용공고는 최신순으로 정렬되어 최대 3개까지 보여진다.  
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = """
                            추천 채용공고 조회 성공
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "apiVersion": "1.0.0",
                                              "timestamp": "2024-08-06T23:12:04+09:00",
                                              "status": "success",
                                              "statusCode": 200,
                                              "message": "요청에 성공하였습니다.",
                                              "data": [
                                                {
                                                  "id": "ef313cca-fd47-4cff-8fcf-8e0909908cdc",
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
                                                  "id": "8ee06894-2893-4efe-bb87-5e5c25e8a319",
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
                                                  "id": "1bc1a634-2cde-4572-b11d-5eb4ac4e423b",
                                                  "title": "jobPost_title",
                                                  "career": "신입",
                                                  "companyName": "jobPost_coname",
                                                  "endDate": "2025-01-01T00:00:00+09:00",
                                                  "techStacks": [
                                                    "java",
                                                    "python"
                                                  ],
                                                  "imgUrl": "http://image.com",
                                                  "siteUrl": "http://test.com"
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
                    description = "존재하지 않는 유저이거나 대화일 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = { @ExampleObject(name = "존재하지 않는 유저일 경우", value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-05T23:02:10+09:00",
                                      "status": "fail",
                                      "statusCode": 404,
                                      "message": "일치하는 유저가 없습니다."
                                    }
                """),
                                    @ExampleObject(name = "존재하지 않는 대화일 경우", value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-05T23:02:10+09:00",
                                      "status": "fail",
                                      "statusCode": 404,
                                      "message": "일치하는 대화가 없습니다."
                                    }
                """),
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
    @GetMapping("/{chatId}/jobposts")
    public ResponseEntity<ApiResponse<List<JobPostRes>>> getTop3JobPostsFromChatAndUser (HttpServletRequest request,
                                                                                         @PathVariable(name = "chatId") UUID chatId) {
        UUID userId = (UUID) sessionService.getUserIdFromSession(request);
        List<JobPostRes> top3JobPostsFromChatAndUser = chatService.getTop3JobPostsFromChatAndUser(chatId, userId);
        return ResponseEntity.status(OK).body(success(SUCCESS, top3JobPostsFromChatAndUser));
    }

    /**
     * 대화 제목 수정 API
     * [PATCH] /v1/chats/:chatId
     *
     * RequestBody
     * - title: 대화 제목
     *
     * @return ResponseEntity<ApiResponse<PatchChatRes>>
     */
    @Operation(summary = "대화 제목 수정 API", description = """
            대화의 제목을 수정한다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = """
                            대화 제목 수정 성공
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = """
                                {
                                  "apiVersion": "1.0.0",
                                  "timestamp": "2024-08-05T23:22:32+09:00",
                                  "status": "success",
                                  "statusCode": 200,
                                  "message": "요청에 성공하였습니다.",
                                  "data": {
                                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                    "title": "string234"
                                  }
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
                    description = "존재하지 않는 대화 내역인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-05T23:02:10+09:00",
                                      "status": "fail",
                                      "statusCode": 404,
                                      "message": "일치하는 대화가 없습니다."
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
    @PatchMapping("/{chatId}")
    public ResponseEntity<ApiResponse<PatchChatRes>> modifyChatTitle (@PathVariable(name = "chatId") UUID chatId,
                                                                 @RequestBody PatchChatReq patchChatReq) {

        PatchChatRes patchChatRes = chatService.modifyChatTitle(chatId, patchChatReq.getTitle());
        return ResponseEntity.status(OK).body(success(SUCCESS, patchChatRes));
    }

    /**
     * 대화 삭제 API
     * [DELETE] /v1/chats/:chatId
     * @return ResponseEntity<ApiResponse<DeleteChatRes>>
     */
    @Operation(summary = "대화 삭제 API", description = """
            대화의 상태를 활성(ACTIVE)에서 비활성(INACTIVE)으로 바꿔준다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = """
                            대화 제목 수정 성공
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                              "apiVersion": "1.0.0",
                                              "timestamp": "2024-08-05T23:36:15+09:00",
                                              "status": "success",
                                              "statusCode": 200,
                                              "message": "요청에 성공하였습니다.",
                                              "data": {
                                                "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                              }
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
                    description = "존재하지 않는 대화 내역인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-05T23:02:10+09:00",
                                      "status": "fail",
                                      "statusCode": 404,
                                      "message": "일치하는 대화가 없습니다."
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
    @DeleteMapping("/{chatId}")
    public ResponseEntity<ApiResponse<DeleteChatRes>> deleteChat (@PathVariable(name = "chatId") UUID chatId) {

        DeleteChatRes deleteChatRes = chatService.deleteChat(chatId);
        return ResponseEntity.status(OK).body(success(SUCCESS, deleteChatRes));
    }

}
