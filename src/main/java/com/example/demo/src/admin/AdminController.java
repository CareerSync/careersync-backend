package com.example.demo.src.admin;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.report.model.PostReportReq;
import com.example.demo.src.report.model.PostReportRes;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/admin")
public class AdminController {

    private final AdminService adminService;
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
}
