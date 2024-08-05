package com.example.demo.src.chat.model;

import com.example.demo.src.jobpost.model.JobPostRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QAItemDto {
    private UUID id;
    private String text;
    private String createdAt;
    private String type; // "question" or "answer"
    private List<JobPostRes> jobPosts;
}
