package com.example.demo.src.post;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.GetUserLogRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PostUserLogTimeReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/posts")
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final MessageUtils messageUtils;

    /**
     * 게시물 등록 API
     * [POST] /app/posts
     * @return BaseResponse<PostPostRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 등록 가능

        PostPostRes postRes = postService.createPost(postPostReq);
        return new BaseResponse<>(postRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 조회 API
     * [GET] /app/posts
     * 특정 유저가 작성한 게시물 조회 API
     * [GET] /app/posts? userId =
     * @return BaseResponse<List<GetPostRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPostRes>> getPosts(@RequestParam(required = false) Long userId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 조회 가능

        if (userId == null) {
            List<GetPostRes> getPosts = postService.getPosts();
            return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
        }

        List<GetPostRes> getPosts = postService.getPostsByUserId(userId);
        return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 1개 조회 API
     * [GET] /app/posts/:postId
     * @return BaseResponse<GetPostRes>
     */
    @ResponseBody
    @GetMapping("/{postId}")
    public BaseResponse<GetPostRes> getPost(@PathVariable("postId") Long postId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 조회 가능

        GetPostRes getPostRes = postService.getPost(postId);
        return new BaseResponse<>(getPostRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 회원 CUD 히스토리 전체 조회
     * [GET] /app/posts/log/history
     *
     * 회원 CUD 히스토리 선택 조회
     * [GET] /app/posts/log/history? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetPostLogRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/log/history")
    public BaseResponse<List<GetPostLogRes>> getPostHistory(@RequestParam(required = false) String revType) {

        if (revType == null) {
            List<GetPostLogRes> postHistoryByTime = postService.getPostHistory();
            return new BaseResponse<>(postHistoryByTime, messageUtils.getMessage("SUCCESS"));
        }

        List<GetPostLogRes> getPostHistoryList = postService.getPostHistoryByRevType(revType);
        return new BaseResponse<>(getPostHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 회원 CUD 히스토리 시간 기준 조회
     * [POST] /app/posts/history/time
     @return BaseResponse<List<GetPostLogRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/log/history/time")
    public BaseResponse<List<GetPostLogRes>> getPostHistoryByTime(@RequestBody PostUserLogTimeReq req) {

        List<GetPostLogRes> getUserHistoryList = postService.getPostHistoryByTime(req);
        return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 내용 수정 API
     * [PATCH] /app/posts/:postId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{postId}")
    public BaseResponse<String> modifyPostContent(@PathVariable("postId") Long postId, @RequestBody PatchPostReq patchPostReq) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 수정 가능

        postService.modifyPostContent(postId, patchPostReq);

        String result = "게시물 내용 수정 완료";
        return new BaseResponse<>(result, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 삭제 API
     * [DELETE] /app/posts/:postId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{postId}")
    public BaseResponse<String> deletePost(@PathVariable("postId") Long postId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 삭제 가능

        postService.deletePost(postId);

        String result = "게시물 삭제 완료";
        return new BaseResponse<>(result, messageUtils.getMessage("SUCCESS"));
    }


}
