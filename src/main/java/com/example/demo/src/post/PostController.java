package com.example.demo.src.post;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
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

        GetPostRes getPosts = postService.getPost(postId);
        return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
    }


}
