package com.example.demo.src.feed;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.feed.model.*;
import com.example.demo.src.user.model.PostUserLogTimeReq;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/feeds")
public class FeedController {

    private final FeedService feedService;
    private final JwtService jwtService;
    private final MessageUtils messageUtils;

    /**
     * 게시물 등록 API
     * [POST] /app/feeds
     * @return BaseResponse<PostFeedRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostFeedRes> createFeed(@RequestBody PostFeedReq postFeedReq) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 등록 가능

        PostFeedRes postRes = feedService.createFeed(postFeedReq);
        return new BaseResponse<>(postRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 조회 API
     * [GET] /app/feeds
     * 특정 유저가 작성한 게시물 조회 API
     * [GET] /app/feeds? userId =
     * @return BaseResponse<List<GetPostRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetFeedRes>> getPosts(@RequestParam(required = false) Long userId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 조회 가능

        if (userId == null) {
            List<GetFeedRes> getPosts = feedService.getFeeds();
            return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
        }

        List<GetFeedRes> getPosts = feedService.getFeedsByUserId(userId);
        return new BaseResponse<>(getPosts, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 1개 조회 API
     * [GET] /app/feeds/:feedId
     * @return BaseResponse<GetPostRes>
     */
    @ResponseBody
    @GetMapping("/{feedId}")
    public BaseResponse<GetFeedRes> getFeed(@PathVariable("feedId") Long feedId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 조회 가능

        GetFeedRes getFeedRes = feedService.getFeed(feedId);
        return new BaseResponse<>(getFeedRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 CUD 히스토리 전체 조회
     * [GET] /app/feeds/log/history
     *
     * 게시물 CUD 히스토리 선택 조회
     * [GET] /app/feeds/log/history? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetFeedLogRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/log/history")
    public BaseResponse<List<GetFeedLogRes>> getPostHistory(@RequestParam(required = false) String revType) {

        if (revType == null) {
            List<GetFeedLogRes> postHistoryByTime = feedService.getFeedHistory();
            return new BaseResponse<>(postHistoryByTime, messageUtils.getMessage("SUCCESS"));
        }

        List<GetFeedLogRes> getPostHistoryList = feedService.getFeedHistoryByRevType(revType);
        return new BaseResponse<>(getPostHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 CUD 히스토리 시간 기준 조회
     * [POST] /app/feeds/history/time
     @return BaseResponse<List<GetFeedLogRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/log/history/time")
    public BaseResponse<List<GetFeedLogRes>> getFeedHistoryByTime(@RequestBody PostUserLogTimeReq req) {

        List<GetFeedLogRes> getUserHistoryList = feedService.getFeedHistoryByTime(req);
        return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 내용 수정 API
     * [PATCH] /app/feeds/:feedId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{feedId}")
    public BaseResponse<String> modifyFeedContent(@PathVariable("feedId") Long feedId, @RequestBody PatchFeedReq patchPostReq) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 수정 가능

        feedService.modifyPostContent(feedId, patchPostReq);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_FEED_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 삭제 API
     * [DELETE] /app/feeds/:feedId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{feedId}")
    public BaseResponse<String> deleteFeed(@PathVariable("feedId") Long feedId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 게시물 삭제 가능

        feedService.deleteFeed(feedId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_FEED_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }


}
