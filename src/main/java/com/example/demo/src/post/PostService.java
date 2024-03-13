package com.example.demo.src.post;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.test.entity.Memo;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PostUserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // POST
    public PostPostRes createPost(PostPostReq req) {

        User user = userRepository.findByIdAndState(req.getUserId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        Post savePost = postRepository.save(req.toEntity(user));
        return new PostPostRes(savePost.getId(), savePost.getContent());
    }
    // GET
    @Transactional(readOnly = true)
    public List<GetPostRes> getPosts() {
        List<GetPostRes> getPostsResList = postRepository.findAllByState(ACTIVE).stream()
                .map(GetPostRes::new)
                .collect(Collectors.toList());

        return getPostsResList;
    }

    @Transactional(readOnly = true)
    public List<GetPostRes> getPostsByUserId(Long userId) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        List<GetPostRes> getPostsResList =  user.getPostList().stream()
                .map(GetPostRes::new)
                .collect(Collectors.toList());

        return getPostsResList;
    }

    @Transactional(readOnly = true)
    public GetPostRes getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(NOT_FIND_POST));
        return new GetPostRes(post);
    }


}
