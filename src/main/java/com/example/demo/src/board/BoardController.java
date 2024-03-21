package com.example.demo.src.board;

import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.board.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "board 도메인", description = "게시물 API")
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
    @Operation(summary = "게시물 등록", description = "입력된 게시물 등록 요청에 따라 게시물을 등록합니다. 이미지 파일은 최소 1장, 최대 10장 업로드 가능합니다.")
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostBoardRes> createBoard(BoardFileVO boardFileVO) throws Exception {

        Long userId = jwtService.getUserId();// 로그인이 정상적으로 이뤄져야 게시물 등록 가능
        PostBoardReq postBoardReq = new PostBoardReq(boardFileVO.getContent(), boardFileVO.isVideo(), boardFileVO.isImageOne());
        PostBoardRes postRes = boardService.createBoard(userId, postBoardReq, boardFileVO.getImages());
        return new BaseResponse<>(postRes, messageUtils.getMessage("SUCCESS"));
    }
    /**
     * 게시물 조회 API
     * [GET] /app/boards
     * 특정 유저가 작성한 게시물 조회 API
     * @return BaseResponse<List<GetBoardRes>>
     */
    @Operation(summary = "유저의 게시물 조회", description = "로그인 한 유저의 게시물을 조회합니다. pageIndex와 size값을 명시하면 페이징 처리가 됩니다.")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetBoardRes>> getBoards(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
                                                     @RequestParam(value = "size", required = false) Integer size) {

        Long userId = jwtService.getUserId();// 로그인이 정상적으로 이뤄져야 게시물 조회 가능

        if (size == null) { // 유저가 작성한 게시물 모두 가져오기
            List<GetBoardRes> getPosts = boardService.getBoardsByUserId(userId);
            return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
        } else { // 명시한 size 만큼 페이징 처리 후 가져오기
            List<GetBoardRes> getPosts = boardService.getBoardsByUserIdWithPaging(userId, pageIndex, size);
            return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
        }

    }

    /**
     * 게시물 1개 조회 API
     * [GET] /app/boards/:boardId
     * @return BaseResponse<GetBoardRes>
     */
    @Operation(summary = "게시물 1개 조회", description = "입력된 boardId값에 해당하는 게시물을 조회합니다.")
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
    @Operation(summary = "게시물 내용 수정", description = "입력된 boardId값에 해당하는 게시물의 내용을 수정합니다.")
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
    @Operation(summary = "게시물 상태 수정", description = "입력된 상태값에 따라 게시물의 상태를 수정합니다.")
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
    @Operation(summary = "게시물 삭제", description = "입력된 boardId값에 해당하는 게시물을 삭제합니다.")
    @ResponseBody
    @DeleteMapping("/{boardId}")
    public BaseResponse<String> deleteBoard(@PathVariable("boardId") Long boardId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 삭제 가능
        boardService.deleteBoard(boardId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_BOARD_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }


}
