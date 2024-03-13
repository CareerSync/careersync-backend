package com.example.demo.src.report;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.report.model.GetReportRes;
import com.example.demo.src.report.model.PostReportReq;
import com.example.demo.src.report.model.PostReportRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostReportRes> createReport(@RequestBody PostReportReq postReportReq) {

        jwtService.getUserId(); // 로그인이 정상적으로 이뤄져야 신고 가능

        PostReportRes postRes = reportService.createReport(postReportReq);
        return new BaseResponse<>(postRes, messageUtils.getMessage("SUCCESS"));
    }

    /**
     * 신고 조회 API
     * [GET] /reports
     * 신고 내역 전체 API
     * [GET] /app/reports
     * @return BaseResponse<List<GetUserRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetReportRes>> getReports() {
        // Get Reports
        List<GetReportRes> getReportRes = reportService.getReports();
        return new BaseResponse<>(getReportRes, messageUtils.getMessage("SUCCESS"));
    }

}
