package com.example.demo.src.admin;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.admin.model.PostFeedLogTimeReq;
import com.example.demo.src.admin.model.PostReportLogTimeReq;
import com.example.demo.src.feed.FeedService;
import com.example.demo.src.feed.model.GetFeedLogRes;
import com.example.demo.src.payment.PaymentService;
import com.example.demo.src.report.ReportService;
import com.example.demo.src.report.model.GetReportLogRes;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetUserLogRes;
import com.example.demo.src.admin.model.PostUserLogTimeReq;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final FeedService feedService;
    private final ReportService reportService;
    private final PaymentService paymentService;
    private final MessageUtils messageUtils;

    /**
     * 신고된 유저 차단 API
     * [GET] /app/admin/blockReportedUsers
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/blockReportedUsers")
    public BaseResponse<String> blockReportedUsers() {

        adminService.blockReportedUsers();
        return new BaseResponse<>("신고된 유저 차단 완료", messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 회원 CUD 히스토리 전체 조회
     * [GET] /app/users/history
     *
     * 회원 CUD 히스토리 선택 조회
     * [GET] /app/admin/history/users? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetUserLogRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/history/users")
    public BaseResponse<List<GetUserLogRes>> getUserHistory(@RequestParam(required = false) String revType) {

        if (revType == null) {
            List<GetUserLogRes> getUserHistoryList = userService.getUserHistory();
            return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
        }

        List<GetUserLogRes> getUserHistoryList = userService.getUserHistoryByRevType(revType);
        return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 회원 CUD 히스토리 시간 기준 조회
     * [POST] /app/admin/history/time/users
     @return BaseResponse<List<GetUserLogRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/history/time/users")
    public BaseResponse<List<GetUserLogRes>> getUserHistoryByTime(@RequestBody PostUserLogTimeReq req) {

        List<GetUserLogRes> getUserHistoryList = userService.getUserHistoryByTime(req);
        return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 CUD 히스토리 전체 조회
     * [GET] /app/feeds/log/history
     *
     * 게시물 CUD 히스토리 선택 조회
     * [GET] /app/history/feeds? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetFeedLogRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/history/feeds")
    public BaseResponse<List<GetFeedLogRes>> getFeedHistory(@RequestParam(required = false) String revType) {

        if (revType == null) {
            List<GetFeedLogRes> postHistoryByTime = feedService.getFeedHistory();
            return new BaseResponse<>(postHistoryByTime, messageUtils.getMessage("SUCCESS"));
        }

        List<GetFeedLogRes> getPostHistoryList = feedService.getFeedHistoryByRevType(revType);
        return new BaseResponse<>(getPostHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 CUD 히스토리 시간 기준 조회
     * [POST] /app/history/time/feeds
     @return BaseResponse<List<GetFeedLogRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/history/time/feeds")
    public BaseResponse<List<GetFeedLogRes>> getFeedHistoryByTime(@RequestBody PostFeedLogTimeReq req) {

        List<GetFeedLogRes> getUserHistoryList = feedService.getFeedHistoryByTime(req);
        return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고 CUD 히스토리 전체 조회
     * [GET] /app/reports/history
     *
     * 신고 CUD 히스토리 선택 조회
     * [GET] /app/reports/history? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetReportLogRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/history/reports")
    public BaseResponse<List<GetReportLogRes>> getReportHistory(@RequestParam(required = false) String revType) {

        if (revType == null) {
            List<GetReportLogRes> getReportHistoryList = reportService.getReportHistory();
            return new BaseResponse<>(getReportHistoryList, messageUtils.getMessage("SUCCESS"));
        }

        List<GetReportLogRes> getReportHistoryList = reportService.getReportHistoryByRevType(revType);
        return new BaseResponse<>(getReportHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고 CUD 히스토리 시간 기준 조회
     * [POST] /app/reports/history/time
     @return BaseResponse<List<GetReportLogRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/history/time/reports")
    public BaseResponse<List<GetReportLogRes>> getReportHistoryByTime(@RequestBody PostReportLogTimeReq req) {

        List<GetReportLogRes> getReportHistoryList = reportService.getReportHistoryByTime(req);
        return new BaseResponse<>(getReportHistoryList, messageUtils.getMessage("SUCCESS"));
    }

}
