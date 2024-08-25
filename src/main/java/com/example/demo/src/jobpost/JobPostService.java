package com.example.demo.src.jobpost;

import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.jobpost.model.GetJobPostRes;
import com.example.demo.src.jobpost.model.JobPostRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class JobPostService {

    private final JobPostRepository jobPostRepository;

    @Transactional(readOnly = true)
    public List<GetJobPostRes> getJobPosts(Pageable pageable) {

        Page<JobPost> jobPostPage = jobPostRepository.findAll(pageable);

        return jobPostPage.stream()
                .map(GetJobPostRes::fromEntity)
                .toList();

    }

}
