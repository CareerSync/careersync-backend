package com.example.demo.src.report.model;

import com.example.demo.src.feed.entity.Feed;
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
public class PostReportReq {
    private Long userId;
    private Long postId;
    private String category;

    public Report toEntity(User user, Feed feed) {
        return Report.builder()
                .user(user)
                .feed(feed)
                .category(category)
                .build();
    }
}
