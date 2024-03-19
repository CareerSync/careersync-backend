package com.example.demo.src.report.model;

import com.example.demo.src.feed.model.GetFeedRes;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.user.model.GetUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReportRes {
    private Long id;
    private GetUserRes user;
    private GetFeedRes post;
    private String category;

    public GetReportRes(Report report) {
        this.id = report.getId();
        this.user = new GetUserRes(report.getUser());
        this.post = new GetFeedRes(report.getFeed());
        this.category = report.getCategory();
    }
}
