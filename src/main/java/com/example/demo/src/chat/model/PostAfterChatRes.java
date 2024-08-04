package com.example.demo.src.chat.model;

import com.example.demo.src.jobpost.entity.JobPostRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class PostAfterChatRes extends PostChatRes {

    public PostAfterChatRes(String answer, List<JobPostRes> jobPosts) {
        super(answer, jobPosts);
    }
}