package com.example.demo.src.feed;

import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.admin.model.PostFeedLogTimeReq;
import com.example.demo.src.admin.model.PostUserLogTimeReq;
import com.example.demo.src.feed.entity.Feed;
import com.example.demo.src.feed.model.*;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
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

@Transactional
@RequiredArgsConstructor
@Service
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final AuditReader auditReader;

    // POST
    public PostFeedRes createFeed(Long userId, PostFeedReq req) {

        User user = userRepository.findByIdAndState(userId, ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        Feed saveFeed = feedRepository.save(req.toEntity(user));
        return new PostFeedRes(saveFeed.getId(), saveFeed.getContent());
    }
    // GET
    @Transactional(readOnly = true)
    public List<GetFeedRes> getFeeds() {
        List<GetFeedRes> getPostsResList = feedRepository.findAllByState(ACTIVE).stream()
                .map(GetFeedRes::new)
                .collect(Collectors.toList());

        return getPostsResList;
    }

    @Transactional(readOnly = true)
    public List<GetFeedRes> getFeedsByUserId(Long userId) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        List<GetFeedRes> getPostsResList =  user.getFeedList().stream()
                .filter(post -> post.getState() == ACTIVE)
                .map(GetFeedRes::new)
                .collect(Collectors.toList());

        return getPostsResList;
    }

    @Transactional(readOnly = true)
    public GetFeedRes getFeed(Long postId) {
        Feed feed = feedRepository.findByIdAndState(postId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_FEED));
        return new GetFeedRes(feed);
    }

    @Transactional(readOnly = true)
    public List<GetFeedLogRes> getFeedHistoryByRevType(String revType) {

        if (!revType.equals("INSERT") && !revType.equals("UPDATE") && !revType.equals("DELETE")) {
            throw new BaseException(REVTYPE_ERROR);
        }

        List<Object> revs = getRevs();

        List<GetFeedLogRes> feedLogs = new ArrayList<>();
        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getFeedLogResByType(feedLogs, revObject.getId(), revType);
        });

        return feedLogs;
    }

    @Transactional(readOnly = true)
    public List<GetFeedLogRes> getFeedHistory() {

        List<Object> revs = getRevs();

        List<GetFeedLogRes> feedLogs = new ArrayList<>();

        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getFeedLogRes(feedLogs, revObject.getId());
        });

        return feedLogs;
    }

    @Transactional(readOnly = true)
    public List<GetFeedLogRes> getFeedHistoryByTime(PostFeedLogTimeReq req) {

        LocalDateTime startTime = req.getStartTime();
        LocalDateTime endTime = req.getEndTime();

        List<Object> revs = getRevs();

        List<GetFeedLogRes> feedLogs = new ArrayList<>();

        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getFeedLogResByTime(feedLogs, revObject.getId(), startTime, endTime);
        });

        return feedLogs;
    }

    private void getFeedLogResByType(List<GetFeedLogRes> feedLogs, Long rev, String revType) {

        String rType = revType;

        Revisions<Long, Feed> revisions = feedRepository.findRevisions(rev);

        for (Revision<Long, Feed> revision : revisions.getContent()) {
            if (String.valueOf(revision.getMetadata().getRevisionType()).equals(rType)) {
                feedLogs.add(makeGetFeedLogRes(revision));
            }
        }
    }

    private void getFeedLogRes(List<GetFeedLogRes> feedLogs, Long rev) {

        Revisions<Long, Feed> revisions = feedRepository.findRevisions(rev);
        for (Revision<Long, Feed> revision : revisions.getContent()) {
            feedLogs.add(makeGetFeedLogRes(revision));
        }
    }

    private void getFeedLogResByTime(List<GetFeedLogRes> feedLogs, Long rev,
                                     LocalDateTime startTime, LocalDateTime endTime) {

        Revisions<Long, Feed> revisions = feedRepository.findRevisions(rev);
        for (Revision<Long, Feed> revision : revisions.getContent()) {
            Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));

            if (!localDateTime.isBefore(startTime) && !localDateTime.isAfter(endTime)) {
                GetFeedLogRes getFeedLogRes = makeGetFeedLogRes(revision);
                feedLogs.add(getFeedLogRes);
            }

        }
    }

    private GetFeedLogRes makeGetFeedLogRes(Revision<Long, Feed> revision) {
        Long revisionNumber = revision.getMetadata().getRevisionNumber().get();
        String revisionType = String.valueOf(revision.getMetadata().getRevisionType());

        Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));
        return new GetFeedLogRes(revisionNumber, revisionType, localDateTime);
    }

    private List<Object> getRevs() {
        return auditReader.createQuery()
                .forRevisionsOfEntity(Feed.class, false, true)
                .getResultList();
    }

    // PATCH
    public void modifyFeedContent(Long postId, PatchFeedReq patchPostReq) {
        Feed feed = feedRepository.findByIdAndState(postId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_FEED));
        feed.updateContent(patchPostReq.getContent());
    }

    public void modifyFeedState(Long postId, State state) {
        Feed feed = feedRepository.findByIdAndState(postId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_FEED));
        feed.updateState(state);
    }


    // DELETE
    public void deleteFeed(Long postId) {
        Feed feed = feedRepository.findByIdAndState(postId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_FEED));
        feedRepository.delete(feed);
    }

}
