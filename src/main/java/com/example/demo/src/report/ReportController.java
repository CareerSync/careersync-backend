package com.example.demo.src.report;

import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.report.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "report 도메인", description = "신고 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/reports")
public class ReportController {

    private final ReportService reportService;
    private final JwtService jwtService;
    private final MessageUtils messageUtils;

    /**
     * 신고 등록 API
     * [POST] /app/reports
     * @return BaseResponse<PostReportRes>
     */
    @Operation(summary = "신고 등록", description = "입력된 신고 등록 요청에 따라 신고 내역을 등록합니다.")
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostReportRes> createReport(@RequestBody PostReportReq postReportReq) {
        Long userId = jwtService.getUserId();// 로그인이 정상적으로 이뤄져야 신고 가능
        PostReportRes postRes = reportService.createReport(userId, postReportReq);
        return new BaseResponse<>(postRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고 조회 API
     * [GET] /app/reports
     * @return BaseResponse<List<GetReportRes>>
     */
    @Operation(summary = "신고 내역 조회", description = "로그인한 유저의 신고 내역을 조회합니다.")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetReportRes>> getReports() {
        Long userId = jwtService.getUserId();
        List<GetReportRes> getReportRes = reportService.getReports(userId);
        return new BaseResponse<>(getReportRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고 내역 1개 조회 API
     * [GET] /app/reports:reportId
     * @return BaseResponse<GetReportRes>
     */
    @Operation(summary = "신고 내역 1개 조회", description = "입력된 reportId에 해당하는 신고 내역을 조회합니다.")
    @ResponseBody
    @GetMapping("/{reportId}")
    public BaseResponse<GetReportRes> getReport(@PathVariable("reportId") Long reportId) {
        // Get Reports
        GetReportRes getReportRes = reportService.getReport(reportId);
        return new BaseResponse<>(getReportRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고된 유저 조회 API
     * [GET] /app/reports/users
     * @return BaseResponse<List<GetReportUserRes>>
     */
    @Operation(summary = "신고당한 유저 조회", description = "신고된 게시물을 작성한 회원들, 즉 신고당한 회원들을 조회합니다.")
    @ResponseBody
    @GetMapping("users")
    public BaseResponse<List<GetReportUserRes>> getReportedUsers() {
        // Get Reports
        List<GetReportUserRes> getReportedUserRes = reportService.getReportedUsers();
        return new BaseResponse<>(getReportedUserRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고내역 카테고리 수정 API
     * [PATCH] /app/reports/:postId
     * @return BaseResponse<String>
     */
    @Operation(summary = "신고 내역 카테고리 수정", description = "입력된 reportId값에 해당하는 신고 내역의 카테고리를 수정합니다.")
    @ResponseBody
    @PatchMapping("/{reportId}")
    public BaseResponse<String> modifyReportCategory(@PathVariable("reportId") Long reportId, @RequestBody PatchReportReq patchReportReq) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 신고 내역 수정 가능
        reportService.modifyReportCategory(reportId, patchReportReq);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_REPORT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고내역 상태 수정 API
     * [PATCH] /app/reports? state=
     * @return BaseResponse<String>
     */
    @Operation(summary = "신고 내역 상태 수정", description = "입력된 reportId값에 해당하는 신고 내역의 상태값을 수정합니다.")
    @ResponseBody
    @PatchMapping("/{reportId}/state")
    public BaseResponse<String> modifyReportState(@PathVariable("reportId") Long reportId, @RequestParam State state) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 신고 내역 수정 가능
        reportService.modifyReportState(reportId, state);
        return new BaseResponse<>(messageUtils.getMessage("MODIFY_REPORT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고내역 삭제 API
     * [DELETE] /app/reports/:reportId
     * @return BaseResponse<String>
     */
    @Operation(summary = "신고 내역 삭제", description = "입력된 reportId값에 해당하는 신고 내역을 삭제합니다.")
    @ResponseBody
    @DeleteMapping("/{reportId}")
    public BaseResponse<String> deleteReport(@PathVariable("reportId") Long reportId) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 신고 내역 삭제 가능
        reportService.deleteReport(reportId);
        return new BaseResponse<>(messageUtils.getMessage("DELETE_REPORT_SUCCESS"), messageUtils.getMessage("SUCCESS"));
    }

}
