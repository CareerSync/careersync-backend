package com.example.demo.src.chat;

import com.example.demo.common.response.ApiResponse;
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

    private final UserService userService;
    private final ChatService chatService;
    private final SessionService sessionService;

    /**
     * 채팅 생성 후 첫 질문에 대한 답변받기 API
     * [POST] /v1/chats
     * @return ResponseEntity<ApiResponse<PostChatRes>>
     */
    @Operation(summary = "채팅 생성 후 첫 질문에 대한 답변받기 API", description = "채팅의 첫 질문에 대한 답변을 반환합니다.")
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
    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createChat (HttpServletRequest request, @RequestBody @Valid PostChatReq postChatReq) {

        UUID chatId = postChatReq.getId();
        UUID userId = (UUID) sessionService.getUserIdFromSession(request);

        if (chatId == null) {
            // 첫 질문이 아닌 경우
            return ResponseEntity.status(OK).body(success(ANSWER_CREATED, "afterChat"));
        } else {
            // 첫 질문인 경우
            PostChatRes firstChat = chatService.createFirstChat(userId, postChatReq);
            return ResponseEntity.status(OK).body(success(CHAT_CREATED, firstChat));
        }

    }

}
