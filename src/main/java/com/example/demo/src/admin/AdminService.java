package com.example.demo.src.admin;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.src.report.ReportRepository;
import com.example.demo.src.report.ReportService;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.GetReportUserRes;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.example.demo.src.user.entity.User.AccountState.*;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {

    private final ReportService reportService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public void blockReportedUsers() {
        reportService.getReportedUsers().stream()
                .map((reportedUser) -> {
                    // 1. 신고 당한 유저들 상태 : ACTIVE -> BLOCKED
                    // 2. 신고 내역들 상태 : ACTIVE -> INACTIVE

                    Long userId = reportedUser.getUserId();
                    Optional<User> findUser = userRepository.findById(userId);
                    if (findUser.isPresent()) {
                        User user = findUser.get();
                        user.updateAccountState(BLOCKED);
                    }
                    Optional<Report> findReport = reportRepository.findById(reportedUser.getId());
                    if (findReport.isPresent()) {
                        Report report = findReport.get();
                        report.updateState(INACTIVE);
                    }
                    return null;
                });

    }
}
