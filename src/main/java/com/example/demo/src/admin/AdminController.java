package com.example.demo.src.admin;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.admin.model.PostPaymentLogTimeReq;
import com.example.demo.src.admin.model.PostReportLogTimeReq;
import com.example.demo.src.board.BoardService;
import com.example.demo.src.board.model.GetBoardLogRes;
import com.example.demo.src.admin.model.PostBoardLogTimeReq;
import com.example.demo.src.payment.PaymentService;
import com.example.demo.src.payment.model.GetPaymentLogRes;
import com.example.demo.src.report.ReportService;
import com.example.demo.src.report.model.GetReportLogRes;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetUserLogRes;
import com.example.demo.src.admin.model.PostUserLogTimeReq;
import com.example.demo.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "admin 도메인", description = "관리자 API, 히스토리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final BoardService boardService;
    private final ReportService reportService;
    private final PaymentService paymentService;
    private final MessageUtils messageUtils;

    /**
     * 신고된 유저 차단 API
     * [GET] /app/admin/blockReportedUsers
     * @return BaseResponse<String>
     */
    @Operation(summary = "신고당한 회원 차단", description = "신고당한 유저들의 상태값을 차단(BLOCKED)으로 수정합니다.")
    @ResponseBody
    @GetMapping("/blockReportedUsers")
    public BaseResponse<String> blockReportedUsers() {

        adminService.blockReportedUsers();
        return new BaseResponse<>("신고된 유저 차단 완료", messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 회원 CUD 히스토리 전체 조회
     * [GET] /app/admin/history/users
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
    @Operation(summary = "회원 CUD 히스토리 조회", description = "회원 CUD 로그를 조회합니다. revType이 INSERT면 Create, UPDATE면 Update, DELETE면 Delete에 해당하는 로그를, 만약 없을 경우 모든 로그를 조회합니다.")
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
    @Operation(summary = "회원 CUD 히스토리 - 시작, 종료시간으로 조회", description = "입력된 시작시간과 종료시간 사이의 회원 CUD 로그를 조회합니다.")
    @ResponseBody
    @PostMapping("/history/time/users")
    public BaseResponse<List<GetUserLogRes>> getUserHistoryByTime(@RequestBody PostUserLogTimeReq req) {

        List<GetUserLogRes> getUserHistoryList = userService.getUserHistoryByTime(req);
        return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 CUD 히스토리 전체 조회
     * [GET] /app/admin/history/boards
     *
     * 게시물 CUD 히스토리 선택 조회
     * [GET] /app/admin/history/boards? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetBoardLogRes>>
     */
    // Path-variable
    @Operation(summary = "게시물 CUD 히스토리 조회", description = "게시물 CUD 로그를 조회합니다. revType이 INSERT면 Create, UPDATE면 Update, DELETE면 Delete에 해당하는 로그를, 만약 없을 경우 모든 로그를 조회합니다.")
    @ResponseBody
    @GetMapping("/history/boards")
    public BaseResponse<List<GetBoardLogRes>> getBoardHistory(@RequestParam(required = false) String revType) {

        if (revType == null) {
            List<GetBoardLogRes> postHistoryByTime = boardService.getBoardHistory();
            return new BaseResponse<>(postHistoryByTime, messageUtils.getMessage("SUCCESS"));
        }

        List<GetBoardLogRes> getPostHistoryList = boardService.getBoardHistoryByRevType(revType);
        return new BaseResponse<>(getPostHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 게시물 CUD 히스토리 시간 기준 조회
     * [POST] /app/admin/history/time/boards
     @return BaseResponse<List<GetBoardLogRes>>
     */
    // Path-variable
    @Operation(summary = "게시물 CUD 히스토리 - 시작, 종료시간으로 조회", description = "입력된 시작시간과 종료시간 사이의 게시물 CUD 로그를 조회합니다.")
    @ResponseBody
    @PostMapping("/history/time/boards")
    public BaseResponse<List<GetBoardLogRes>> getBoardHistoryByTime(@RequestBody PostBoardLogTimeReq req) {

        List<GetBoardLogRes> getUserHistoryList = boardService.getBoardHistoryByTime(req);
        return new BaseResponse<>(getUserHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고 CUD 히스토리 전체 조회
     * [GET] /app/admin/history/reports
     *
     * 신고 CUD 히스토리 선택 조회
     * [GET] /app/admin/history/reports? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetReportLogRes>>
     */
    // Path-variable
    @Operation(summary = "신고 CUD 히스토리 조회", description = "신고 CUD 로그를 조회합니다. revType이 INSERT면 Create, UPDATE면 Update, DELETE면 Delete에 해당하는 로그를, 만약 없을 경우 모든 로그를 조회합니다.")
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
     * [POST] /app/admin/history/time/reports
     @return BaseResponse<List<GetReportLogRes>>
     */
    // Path-variable
    @Operation(summary = "신고 CUD 히스토리 - 시작, 종료시간으로 조회", description = "입력된 시작시간과 종료시간 사이의 신고 CUD 로그를 조회합니다.")
    @ResponseBody
    @PostMapping("/history/time/reports")
    public BaseResponse<List<GetReportLogRes>> getReportHistoryByTime(@RequestBody PostReportLogTimeReq req) {

        List<GetReportLogRes> getPaymentHistoryList = reportService.getReportHistoryByTime(req);
        return new BaseResponse<>(getPaymentHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 결제 CUD 히스토리 전체 조회
     * [GET] /app/admin/history/payments
     *
     * 결제 CUD 히스토리 선택 조회
     * [GET] /app/admin/history/payments? revType=
     * revType 종류
     * - Create: INSERT
     * - Update: UPDATE
     * - Delete: DELETE
     * @return BaseResponse<List<GetPaymentLogRes>>
     */
    // Path-variable
    @Operation(summary = "결제 CUD 히스토리 조회", description = "결제 CUD 로그를 조회합니다. revType이 INSERT면 Create, UPDATE면 Update, DELETE면 Delete에 해당하는 로그를, 만약 없을 경우 모든 로그를 조회합니다.")
    @ResponseBody
    @GetMapping("/history/payments")
    public BaseResponse<List<GetPaymentLogRes>> getPaymentHistory(@RequestParam(required = false) String revType) {

        if (revType == null) {
            List<GetPaymentLogRes> getReportHistoryList = paymentService.getPaymentHistory();
            return new BaseResponse<>(getReportHistoryList, messageUtils.getMessage("SUCCESS"));
        }

        List<GetPaymentLogRes> getReportHistoryList = paymentService.getPaymentHistoryByRevType(revType);
        return new BaseResponse<>(getReportHistoryList, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 결제 CUD 히스토리 시간 기준 조회
     * [POST] /app/admin/history/payments/time
     @return BaseResponse<List<GetPaymentLogRes>>
     */
    // Path-variable
    @Operation(summary = "결제 CUD 히스토리 - 시작, 종료시간으로 조회", description = "입력된 시작시간과 종료시간 사이의 결제 CUD 로그를 조회합니다.")
    @ResponseBody
    @PostMapping("/history/time/payments")
    public BaseResponse<List<GetPaymentLogRes>> getPaymentHistoryByTime(@RequestBody PostPaymentLogTimeReq req) {

        List<GetPaymentLogRes> paymentHistoryByTime = paymentService.getPaymentHistoryByTime(req);
        return new BaseResponse<>(paymentHistoryByTime, messageUtils.getMessage("SUCCESS"));
    }

}
