package com.example.demo.src.report.model;

import com.example.demo.src.post.entity.Post;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportReq {
    private Long userId;
    private Long postId;
    private String category;

    public Report toEntity(User user, Post post, String category) {
        return Report.builder()
                .user(user)
                .post(post)
                .category(category)
                .build();
    }
}
