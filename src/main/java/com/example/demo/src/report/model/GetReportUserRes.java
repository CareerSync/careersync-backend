package com.example.demo.src.report.model;

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
public class GetReportUserRes {
    private Long id;
    private GetUserRes user;
    private String category;

    public GetReportUserRes(Report report, User user) {
        this.id = report.getId();
        this.user = new GetUserRes(user);
        this.category = report.getCategory();
    }

    public Long getUserId() {
        return user.getId();
    }
}
