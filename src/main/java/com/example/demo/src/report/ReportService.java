package com.example.demo.src.report;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.post.PostRepository;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.post.model.PatchPostReq;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.*;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PostRepository postRepository;

    // POST
    public PostReportRes createReport(PostReportReq req) {

        // 유저와 게시글 -> 둘 다 ACTIVE한 상태여야 한다.
        User user = userRepository.findByIdAndState(req.getUserId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        Post post = postRepository.findByIdAndState(req.getPostId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_POST));

        // 이미 신고한 내역 있으면 중복 신고 안되도록 처리
        Optional<Report> checkReport = reportRepository.findByUserIdAndPostId(user.getId(), post.getId());
        if(checkReport.isPresent()){
            throw new BaseException(POST_REPORT_EXISTS_USER_AND_POST);
        }

        Report saveReport = reportRepository.save(req.toEntity(user, post));
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
                .map(report -> new GetReportUserRes(report, report.getReportedUser(report.getPost())))
                .collect(Collectors.toList());

        return getReportedUsers;
    }

    @Transactional(readOnly = true)
    public GetReportRes getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BaseException(NOT_FIND_REPORT));

        return new GetReportRes(report);
    }

    // PATCH
    public void modifyReportCategory(Long reportId, PatchReportReq patchReportReq) {
        Report report = reportRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_POST));
        report.updateCategory(patchReportReq.getCategory());
    }

    // DELETE
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_POST));
        report.deleteReport();
    }

}
