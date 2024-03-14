package com.example.demo.src.post;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.post.model.*;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.GetReportRes;
import com.example.demo.src.test.entity.Memo;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
    public List<GetPostLogRes> getPostHistoryByRevType(String revType) {

        if (!revType.equals("INSERT") && !revType.equals("UPDATE") && !revType.equals("DELETE")) {
            throw new BaseException(REVTYPE_ERROR);
        }

        List<Long> revIds = getRevIds();

        List<GetPostLogRes> postLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getPostLogResByType(postLogs, id, revType);
                });

        return postLogs;
    }

    @Transactional(readOnly = true)
    public List<GetPostLogRes> getPostHistory() {

        List<Long> revIds = getRevIds();

        List<GetPostLogRes> postLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getPostLogRes(postLogs, id);
                });

        return postLogs;
    }

    @Transactional(readOnly = true)
    public List<GetPostLogRes> getPostHistoryByTime(PostUserLogTimeReq req) {

        LocalDateTime startTime = req.getStartTime();
        LocalDateTime endTime = req.getEndTime();

        List<Long> revIds = getRevIds();

        List<GetPostLogRes> postLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getPostLogResByTime(postLogs, id, startTime, endTime);
                });

        return postLogs;
    }

    private void getPostLogResByType(List<GetPostLogRes> postLogs, Long rev, String revType) {

        String rType = revType;

        Revisions<Long, Post> revisions = postRepository.findRevisions(rev);

        for (Revision<Long, Post> revision : revisions.getContent()) {
            if (String.valueOf(revision.getMetadata().getRevisionType()).equals(rType)) {
                postLogs.add(makeGetPostLogRes(revision));
            }
        }
    }

    private void getPostLogRes(List<GetPostLogRes> postLogs, Long rev) {

        Revisions<Long, Post> revisions = postRepository.findRevisions(rev);
        for (Revision<Long, Post> revision : revisions.getContent()) {
            postLogs.add(makeGetPostLogRes(revision));
        }
    }

    private void getPostLogResByTime(List<GetPostLogRes> postLogs, Long rev,
                                     LocalDateTime startTime, LocalDateTime endTime) {

        Revisions<Long, Post> revisions = postRepository.findRevisions(rev);
        for (Revision<Long, Post> revision : revisions.getContent()) {
            Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));

            if (!localDateTime.isBefore(startTime) && !localDateTime.isAfter(endTime)) {
                GetPostLogRes getPostLogRes = makeGetPostLogRes(revision);
                postLogs.add(getPostLogRes);
            }

        }
    }

    private GetPostLogRes makeGetPostLogRes(Revision<Long, Post> revision) {
        Long revisionNumber = revision.getMetadata().getRevisionNumber().get();
        String revisionType = String.valueOf(revision.getMetadata().getRevisionType());

        Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));
        return new GetPostLogRes(revisionNumber, revisionType, localDateTime);
    }

    private List<Long> getRevIds() {
        return auditReader.createQuery()
                .forRevisionsOfEntity(Post.class, false, false)
                .addProjection(AuditEntity.id())
                .getResultList();
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
