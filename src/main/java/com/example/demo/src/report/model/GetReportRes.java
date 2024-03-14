package com.example.demo.src.report.model;

import com.example.demo.src.post.entity.Post;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.user.entity.User;
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
    private GetPostRes post;
    private String category;

    public GetReportRes(Report report) {
        this.id = report.getId();
        this.user = new GetUserRes(report.getUser());
        this.post = new GetPostRes(report.getPost());
        this.category = report.getCategory();
    }
}
