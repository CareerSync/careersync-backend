package com.example.demo.src.report;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.post.PostRepository;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.report.model.PostReportReq;
import com.example.demo.src.report.model.PostReportRes;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.INVALID_POST;
import static com.example.demo.common.response.BaseResponseStatus.INVALID_USER;

@Transactional
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // POST
    public PostReportRes createPost(PostReportReq req) {

        User user = userRepository.findByIdAndState(req.getUserId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        Post post = postRepository.findByIdAndState(req.getPostId(), ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_POST));

        Report saveReport = reportRepository.save(req.toEntity(user, post));
        return new PostReportRes(saveReport.getId(), saveReport.getCategory());

    }

}
