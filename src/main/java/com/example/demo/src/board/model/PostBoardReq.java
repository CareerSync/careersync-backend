package com.example.demo.src.board.model;

import com.example.demo.src.board.entity.Board;
import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostBoardReq {
    private String content;

    public Board toEntity(User user) {
        return Board.builder()
                .user(user)
                .content(content)
                .build();
    }
}
