package com.example.demo.src.post;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.model.PatchPostReq;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.GetReportRes;
import com.example.demo.src.test.entity.Memo;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserRes;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static org.hibernate.envers.RevisionType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuditReader auditReader;

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
                .filter(post -> post.getState() == ACTIVE)
                .map(GetPostRes::new)
                .collect(Collectors.toList());

        return getPostsResList;
    }

    @Transactional(readOnly = true)
    public GetPostRes getPost(Long postId) {
        Post post = postRepository.findByIdAndState(postId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_POST));
        return new GetPostRes(post);
    }

    @Transactional(readOnly = true)
    public List<GetPostRes> getPostHistory(Long revId) {

        if (revId == 0) {
            List<Post> resultList = auditReader.createQuery()
                    .forRevisionsOfEntity(Post.class, true, true)
                    .add(AuditEntity.revisionType().eq(ADD))
                    .getResultList();

            return resultList.stream()
                    .map(GetPostRes::new)
                    .collect(Collectors.toList());
        }

        if (revId == 1) {
            List<Post> resultList = auditReader.createQuery()
                    .forRevisionsOfEntity(Post.class, true, true)
                    .add(AuditEntity.revisionType().eq(MOD))
                    .getResultList();

            return resultList.stream()
                    .map(GetPostRes::new)
                    .collect(Collectors.toList());
        }

        if(revId == 2){
            List<Post> resultList = auditReader.createQuery()
                    .forRevisionsOfEntity(Post.class, true, true)
                    .add(AuditEntity.revisionType().eq(DEL))
                    .getResultList();

            return resultList.stream()
                    .map(GetPostRes::new)
                    .collect(Collectors.toList());
        }

        else {
            throw new BaseException(REVTYPE_ERROR);
        }
    }

    // PATCH
    public void modifyPostContent(Long postId, PatchPostReq patchPostReq) {
        Post post = postRepository.findByIdAndState(postId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_POST));
        post.updateContent(patchPostReq.getContent());
    }

    // DELETE
    public void deletePost(Long postId) {
        Post post = postRepository.findByIdAndState(postId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_POST));
        post.deletePost();
    }

}
