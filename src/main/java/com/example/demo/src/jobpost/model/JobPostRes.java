package com.example.demo.src.jobpost.model;

import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.jobpost.entity.JobPostTechStack;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostRes {
    private UUID id;
    private String title;
    private String career;
    private String companyName;
    private String endDate;
    private List<String> techStacks;
    private String imgUrl;
    private String siteUrl;

    public static JobPostRes fromEntity(JobPost jobPost) {
        if (jobPost == null) {
            return null; // Handle null input gracefully
        }

        List<String> techStackNames = jobPost.getJobPostTechStacks().stream()
                .map(JobPostTechStack::getName)
                .collect(Collectors.toList());

        return new JobPostRes(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getCareer(),
                jobPost.getCompanyName(),
                jobPost.getEndDate(),
                techStackNames,
                jobPost.getImageUrl(),
                jobPost.getSiteUrl()
        );
    }

    public static List<JobPostRes> fromEntityList(List<JobPost> jobPosts) {
        if (jobPosts == null || jobPosts.isEmpty()) {
            return List.of();
        }

        return jobPosts.stream()
                .map(JobPostRes::fromEntity)
                .collect(Collectors.toList());
    }

}
