package com.example.demo.src.post;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/posts")
public class PostController {

    private final PostService postService;

    private final MessageUtils messageUtils;

    /**
     * 게시물 등록 API
     * [POST] /app/posts
     * @return BaseResponse<PostPostRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq) {
        PostPostRes postRes = postService.createPost(postPostReq);
        return new BaseResponse<>(postRes, messageUtils.getMessage("SUCCESS"));
    }
}
