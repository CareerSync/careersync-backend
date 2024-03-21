package com.example.demo.src.report.model;

import com.example.demo.src.board.entity.Board;
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
    private Long postId;
    private String category;

    public Report toEntity(User user, Board board) {
        return Report.builder()
                .user(user)
                .board(board)
                .category(category)
                .build();
    }
}
