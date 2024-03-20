package com.example.demo.src.board;

import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.board.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/boards")
public class BoardController {

    private final BoardService boardService;
    private final JwtService jwtService;
    private final MessageUtils messageUtils;

    /**
     * 게시물 등록 API
     * [POST] /app/boards
     * @return BaseResponse<PostBoardRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostBoardRes> createBoard(@RequestBody PostBoardReq postBoardReq) {

        Long userId = jwtService.getUserId();// 로그인이 정상적으로 이뤄져야 게시물 등록 가능
        PostBoardRes postRes = boardService.createBoard(userId, postBoardReq);
        return new BaseResponse<>(postRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 조회 API
     * [GET] /app/boards
     * 특정 유저가 작성한 게시물 조회 API
     * @return BaseResponse<List<GetBoardRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetBoardRes>> getBoards() {

        Long userId = jwtService.getUserId();// 로그인이 정상적으로 이뤄져야 게시물 조회 가능
        List<GetBoardRes> getPosts = boardService.getBoardsByUserId(userId);
        return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 1개 조회 API
     * [GET] /app/boards/:boardId
     * @return BaseResponse<GetBoardRes>
     */
    @ResponseBody
    @GetMapping("/{boardId}")
    public BaseResponse<GetBoardRes> getBoard(@PathVariable("boardId") Long boardId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 조회 가능
        GetBoardRes getBoardRes = boardService.getBoard(boardId);
        return new BaseResponse<>(getBoardRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 내용 수정 API
     * [PATCH] /app/boards/:boardId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{boardId}")
    public BaseResponse<String> modifyBoardContent(@PathVariable("boardId") Long boardId, @RequestBody PatchBoardReq patchPostReq) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 수정 가능
        boardService.modifyBoardContent(boardId, patchPostReq);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_BOARD_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 상태 수정 API
     * [PATCH] /app/boards? state=
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{boardId}/state")
    public BaseResponse<String> modifyBoardState(@PathVariable("boardId") Long boardId, @RequestParam("state") State state) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 수정 가능
        boardService.modifyBoardState(boardId, state);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_BOARD_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 삭제 API
     * [DELETE] /app/boards/:boardId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{boardId}")
    public BaseResponse<String> deleteBoard(@PathVariable("boardId") Long boardId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 삭제 가능
        boardService.deleteBoard(boardId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_BOARD_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }


}
