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
@AllArgsConstructor
public class PostChatRes {

    private String title;
    private String answer;
    private List<JobPostRes> jobPosts;

    public PostChatRes(String answer, List<JobPostRes> jobPosts) {
        this.answer = answer;
        this.jobPosts = jobPosts;
    }
}
