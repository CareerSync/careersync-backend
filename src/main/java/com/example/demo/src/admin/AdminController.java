package com.example.demo.src.admin;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.feed.FeedService;
import com.example.demo.src.payment.PaymentService;
import com.example.demo.src.report.ReportService;
import com.example.demo.src.report.model.PostReportReq;
import com.example.demo.src.report.model.PostReportRes;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetUserLogRes;
import com.example.demo.src.user.model.PostUserLogTimeReq;
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

}
