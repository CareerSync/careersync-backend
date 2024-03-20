package com.example.demo.src.report;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.feed.FeedRepository;
import com.example.demo.src.feed.entity.Feed;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.*;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.PostUserLogTimeReq;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final AuditReader auditReader;

    // POST
    public PostReportRes createReport(PostReportReq req) {

        // 유저와 게시글 -> 둘 다 ACTIVE한 상태여야 한다.
        User user = userRepository.findByIdAndState(req.getUserId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        Feed feed = feedRepository.findByIdAndState(req.getPostId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_POST));

        // 이미 신고한 내역 있으면 중복 신고 안되도록 처리
        Optional<Report> checkReport = reportRepository.findByUserIdAndFeedId(user.getId(), feed.getId());
        if(checkReport.isPresent()){
            throw new BaseException(POST_REPORT_EXISTS_USER_AND_POST);
        }

        Report saveReport = reportRepository.save(req.toEntity(user, feed));
        return new PostReportRes(saveReport.getId(), saveReport.getCategory());

    }

    // GET
    @Transactional(readOnly = true)
    public List<GetReportRes> getReports() {
        List<GetReportRes> getReportResList = reportRepository.findAllByState(ACTIVE).stream()
                .map(GetReportRes::new)
                .collect(Collectors.toList());

        return getReportResList;
    }

    @Transactional(readOnly = true)
    public List<GetReportUserRes> getReportedUsers() {
        List<GetReportUserRes> getReportedUsers = reportRepository.findAllByState(ACTIVE).stream()
                .map(report -> new GetReportUserRes(report, report.getReportedUser(report.getFeed())))
                .collect(Collectors.toList());

        return getReportedUsers;
    }

    @Transactional(readOnly = true)
    public GetReportRes getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT));

        return new GetReportRes(report);
    }

    @Transactional(readOnly = true)
    public List<GetReportLogRes> getReportHistoryByRevType(String revType) {

        if (!revType.equals("INSERT") && !revType.equals("UPDATE") && !revType.equals("DELETE")) {
            throw new BaseException(REVTYPE_ERROR);
        }

        List<Long> revIds = getRevIds();

        List<GetReportLogRes> userLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getReportLogResByType(userLogs, id, revType);
                });

        return userLogs;
    }

    @Transactional(readOnly = true)
    public List<GetReportLogRes> getReportHistory() {

        List<Long> revIds = getRevIds();

        List<GetReportLogRes> reportLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getReportLogRes(reportLogs, id);
                });

        return reportLogs;
    }

    @Transactional(readOnly = true)
    public List<GetReportLogRes> getReportHistoryByTime(PostUserLogTimeReq req) {

        LocalDateTime startTime = req.getStartTime();
        LocalDateTime endTime = req.getEndTime();

        List<Long> revIds = getRevIds();

        List<GetReportLogRes> reportLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getReportLogResByTime(reportLogs, id, startTime, endTime);
                });

        return reportLogs;
    }

    private void getReportLogResByType(List<GetReportLogRes> userLogs, Long rev, String revType) {

        String rType = revType;

        Revisions<Long, Report> revisions = reportRepository.findRevisions(rev);

        for (Revision<Long, Report> revision : revisions.getContent()) {
            if (String.valueOf(revision.getMetadata().getRevisionType()).equals(rType)) {
                userLogs.add(makeGetReportLogRes(revision));
            }
        }
    }

    private void getReportLogRes(List<GetReportLogRes> reportLogs, Long rev) {

        Revisions<Long, Report> revisions = reportRepository.findRevisions(rev);
        for (Revision<Long, Report> revision : revisions.getContent()) {
            reportLogs.add(makeGetReportLogRes(revision));
        }
    }

    private void getReportLogResByTime(List<GetReportLogRes> reportLogs, Long rev,
                                     LocalDateTime startTime, LocalDateTime endTime) {

        Revisions<Long, Report> revisions = reportRepository.findRevisions(rev);
        for (Revision<Long, Report> revision : revisions.getContent()) {
            Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));

            if (!localDateTime.isBefore(startTime) && !localDateTime.isAfter(endTime)) {
                GetReportLogRes getUserLogRes = makeGetReportLogRes(revision);
                reportLogs.add(getUserLogRes);
            }

        }
    }

    private GetReportLogRes makeGetReportLogRes(Revision<Long, Report> revision) {
        Long revisionNumber = revision.getMetadata().getRevisionNumber().get();
        String revisionType = String.valueOf(revision.getMetadata().getRevisionType());

        Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));
        return new GetReportLogRes(revisionNumber, revisionType, localDateTime);
    }

    private List<Long> getRevIds() {
        return auditReader.createQuery()
                .forRevisionsOfEntity(Report.class, false, true)
                .addProjection(AuditEntity.id())
                .getResultList();
    }

    // PATCH
    public void modifyReportCategory(Long reportId, PatchReportReq patchReportReq) {
        Report report = reportRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT));
        report.updateCategory(patchReportReq.getCategory());
    }

    public void modifyReportState(Long reportId, State state) {
        Report report = reportRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT));
        report.updateState(state);
    }

    // DELETE
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT));
        reportRepository.delete(report);
    }

}
