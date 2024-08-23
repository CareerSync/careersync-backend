package com.example.demo.src.chat;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.chat.ChatApiResponse;
import com.example.demo.common.response.common.CommonApiResponse;
import com.example.demo.common.response.user.UserApiResponse;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.chat.model.*;
import com.example.demo.src.jobpost.model.JobPostRes;
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
                                    @ExampleObject(name = "일반 질문인 경우", value = ChatApiResponse.CHAT_CREATED),
                                    @ExampleObject(name = "채용 공고 질문인 경우", value = ChatApiResponse.CHAT_CREATED_WITH_JOB_POST)
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
                                    @ExampleObject(name = "질문이 null 이거나 빈 문자열인 경우", value = ChatApiResponse.CHAT_QUESTION_EMPTY),
                                    @ExampleObject(name = "이미 존재하는 대화 식별자인 경우", value = ChatApiResponse.CHAT_ID_EXIST)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "로그인 한 유저 정보가 데이터베이스에 없는 경우",
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
    @PostMapping("")
    public ResponseEntity<ApiResponse<PostChatRes>> createChat (HttpServletRequest request, @RequestBody @Valid PostChatReq postChatReq) throws JsonProcessingException {

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
                                    @ExampleObject(name = "일반 질문인 경우", value = ChatApiResponse.CHAT_CREATED_CONTINUE),
                                    @ExampleObject(name = "채용 공고 질문인 경우", value = ChatApiResponse.CHAT_CREATED_CONTINUE_WITH_JOB_POST)

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
                                    @ExampleObject(value = ChatApiResponse.CHAT_QUESTION_EMPTY)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
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
                            examples = {@ExampleObject(name = "유저가 존재하지 않는 경우", value = UserApiResponse.NOT_FIND_USER),
                                    @ExampleObject(name = "대화가 존재하지 않는 경우", value = ChatApiResponse.NOT_FIND_CHAT)
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
    @PostMapping("/{chatId}")
    public ResponseEntity<ApiResponse<?>> addAnswerToChat (HttpServletRequest request, @PathVariable(name = "chatId") UUID chatId,
                                                           @RequestBody @Valid PostAfterChatReq postAfterChatReq) throws JsonProcessingException {

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
                                    @ExampleObject(value = ChatApiResponse.GET_ALL_CHATS_SUCCESS)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
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
                                    @ExampleObject(value = ChatApiResponse.GET_CHAT_SUCCESS)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 대화 내역인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = ChatApiResponse.NOT_FIND_CHAT)
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
    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<GetChatRes>> getChat (HttpServletRequest request,
                                                            @PathVariable(name = "chatId") UUID chatId) throws JsonProcessingException {

        UUID userId = (UUID) sessionService.getUserIdFromSession(request);
        GetChatRes chatRes = chatService.getChat(userId, chatId);
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
                                    @ExampleObject(value = ChatApiResponse.GET_CHAT_JOB_POST_SUCCESS)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = """
                    path variable인 `chatId`에 UUID값을 넣지 않을 경우 에러 반환
                    """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "METHOD_ARGUMENT_TYPE_MISMATCH", value = ChatApiResponse.WRONG_CHAT_ID_TYPE)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 유저이거나 대화일 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = { @ExampleObject(name = "존재하지 않는 유저일 경우", value = UserApiResponse.NOT_FIND_USER),
                                    @ExampleObject(name = "존재하지 않는 대화일 경우", value = ChatApiResponse.NOT_FIND_CHAT),
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
                                    @ExampleObject(value = ChatApiResponse.CHAT_MODIFY_SUCCESS)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "수정할 대화 제목이 null인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = ChatApiResponse.WRONG_CHAT_TITLE_TYPE)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 대화 내역인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = ChatApiResponse.NOT_FIND_CHAT)
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
    @PatchMapping("/{chatId}")
    public ResponseEntity<ApiResponse<PatchChatRes>> modifyChatTitle (@PathVariable(name = "chatId") UUID chatId,
                                                                 @RequestBody @Valid PatchChatReq patchChatReq) {

        PatchChatRes patchChatRes = chatService.modifyChatTitle(chatId, patchChatReq.getTitle());
        return ResponseEntity.status(OK).body(success(SUCCESS, patchChatRes));
    }

    /**
     * 대화 삭제 API
     * [DELETE] /v1/chats/:chatId
     * @return ResponseEntity<ApiResponse<DeleteChatRes>>
     */
    @Operation(summary = "대화 삭제 API", description = """
            대화의 상태를 활성(ACTIVE)에서 삭제됨(DELETED)으로 바꿔준다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "대화 삭제 성공(실제 데이터 삭제가 아닌, 상태값이 ACTIVE에서 DELETED로 변경)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = ChatApiResponse.CHAT_DELETED_SUCCESS)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 대화 내역인 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = ChatApiResponse.NOT_FIND_CHAT)
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
    @DeleteMapping("/{chatId}")
    public ResponseEntity<ApiResponse<DeleteChatRes>> deleteChat (@PathVariable(name = "chatId") UUID chatId) {

        DeleteChatRes deleteChatRes = chatService.deleteChat(chatId);
        return ResponseEntity.status(OK).body(success(SUCCESS, deleteChatRes));
    }

}
