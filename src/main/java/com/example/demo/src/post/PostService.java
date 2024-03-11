package com.example.demo.src.post;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.test.entity.Memo;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.PostUserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.INVALID_USER;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // POST
    private PostPostRes createPost(PostPostReq req) {

        User user = userRepository.findByIdAndState(req.getUserId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        Post savePost = postRepository.save(req.toEntity(user));
        return new PostPostRes(savePost.getId());
    }


}
