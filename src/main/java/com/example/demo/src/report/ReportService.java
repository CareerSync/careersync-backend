package com.example.demo.src.report;

import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.admin.model.PostReportLogTimeReq;
import com.example.demo.src.board.BoardRepository;
import com.example.demo.src.board.entity.Board;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.*;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
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
import static com.example.demo.common.entity.BaseEntity.State.INACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final AuditReader auditReader;
    private final MessageUtils messageUtils;

    // POST
    public PostReportRes createReport(Long userId, PostReportReq req) {

        // 유저와 게시글 -> 둘 다 ACTIVE한 상태여야 한다.
        User user = userRepository.findByIdAndState(userId, ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        Board board = boardRepository.findByIdAndState(req.getPostId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_BOARD));

        // 이미 신고한 내역 있으면 중복 신고 안되도록 처리
        Optional<Report> checkReport = reportRepository.findByUserIdAndBoardId(user.getId(), board.getId());
        if(checkReport.isPresent()){
            throw new BaseException(POST_REPORT_EXISTS_USER_AND_BOARD, messageUtils.getMessage("POST_REPORT_EXISTS_USER_AND_BOARD"));
        }

        Report saveReport = reportRepository.save(req.toEntity(user, board));
        board.updateState(INACTIVE);
        return new PostReportRes(saveReport.getId(), saveReport.getCategory());

    }

    // GET
    @Transactional(readOnly = true)
    public List<GetReportRes> getReports(Long userId) {
        User user = userRepository.findByIdAndState(userId, ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER, messageUtils.getMessage("INVALID_USER")));

        List<GetReportRes> getReportResList = reportRepository.findAllByUserAndState(user, ACTIVE).stream()
                .map(GetReportRes::new)
                .collect(Collectors.toList());

        return getReportResList;
    }

    @Transactional(readOnly = true)
    public List<GetReportUserRes> getReportedUsers() {
        List<GetReportUserRes> getReportedUsers = reportRepository.findAllByState(ACTIVE).stream()
                .map(report -> new GetReportUserRes(report, report.getReportedUser(report.getBoard())))
                .collect(Collectors.toList());

        return getReportedUsers;
    }

    @Transactional(readOnly = true)
    public GetReportRes getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT, messageUtils.getMessage("NOT_FIND_REPORT")));

        return new GetReportRes(report);
    }

    @Transactional(readOnly = true)
    public List<GetReportLogRes> getReportHistoryByRevType(String revType) {

        if (!revType.equals("INSERT") && !revType.equals("UPDATE") && !revType.equals("DELETE")) {
            throw new BaseException(REVTYPE_ERROR, messageUtils.getMessage("REVTYPE_ERROR"));
        }

        List<Object> revs = getRevs();

        List<GetReportLogRes> reportLogs = new ArrayList<>();

        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getReportLogResByType(reportLogs, revObject.getId(), revType);
        });

        return reportLogs;
    }

    @Transactional(readOnly = true)
    public List<GetReportLogRes> getReportHistory() {

        List<Object> revs = getRevs();

        List<GetReportLogRes> reportLogs = new ArrayList<>();

        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getReportLogRes(reportLogs, revObject.getId());
        });

        return reportLogs;
    }

    @Transactional(readOnly = true)
    public List<GetReportLogRes> getReportHistoryByTime(PostReportLogTimeReq req) {

        LocalDateTime startTime = req.getStartTime();
        LocalDateTime endTime = req.getEndTime();

        List<Object> revs = getRevs();

        List<GetReportLogRes> reportLogs = new ArrayList<>();

        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getReportLogResByTime(reportLogs, revObject.getId(), startTime, endTime);
        });

        return reportLogs;
    }

    private void getReportLogResByType(List<GetReportLogRes> reportLogs, Long rev, String revType) {

        String rType = revType;

        Revisions<Long, Report> revisions = reportRepository.findRevisions(rev);

        for (Revision<Long, Report> revision : revisions.getContent()) {
            if (String.valueOf(revision.getMetadata().getRevisionType()).equals(rType)) {
                reportLogs.add(makeGetReportLogRes(revision));
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

    private List<Object> getRevs() {
        return auditReader.createQuery()
                .forRevisionsOfEntity(Report.class, false, true)
                .getResultList();
    }

    // PATCH
    public void modifyReportCategory(Long reportId, PatchReportReq patchReportReq) {
        Report report = reportRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT, messageUtils.getMessage("NOT_FIND_REPORT")));
        report.updateCategory(patchReportReq.getCategory());
    }

    public void modifyReportState(Long reportId, State state) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT, messageUtils.getMessage("NOT_FIND_REPORT")));
        report.updateState(state);
    }

    // DELETE
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT, messageUtils.getMessage("NOT_FIND_REPORT")));
        reportRepository.delete(report);
    }

}
