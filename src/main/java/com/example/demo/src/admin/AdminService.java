package com.example.demo.src.admin;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.report.ReportRepository;
import com.example.demo.src.report.ReportService;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.GetReportUserRes;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.example.demo.common.response.BaseResponseStatus.NOT_FIND_REPORT;
import static com.example.demo.common.response.BaseResponseStatus.NOT_FIND_USER;
import static com.example.demo.src.user.entity.User.AccountState.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {

    private final ReportService reportService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final MessageUtils messageUtils;

    public void blockReportedUsers() {
        reportService.getReportedUsers()
                .forEach((reportedUser) -> {
                    // 1. 신고 당한 유저들 상태 : ACTIVE -> BLOCKED
                    // 2. 신고 내역들 상태 : ACTIVE -> INACTIVE

                    Long userId = reportedUser.getUserId();
                    User user = userRepository.findById(userId).
                            orElseThrow(() -> new BaseException(NOT_FIND_USER, messageUtils.getMessage("NOT_FIND_USER")));;
                    user.updateAccountState(BLOCKED);

                    Long reportId = reportedUser.getId();
                    Report report = reportRepository.findById(reportId)
                            .orElseThrow(() -> new BaseException(NOT_FIND_REPORT, messageUtils.getMessage("NOT_FIND_REPORT")));
                    report.updateState(INACTIVE);
                });

    }
}
