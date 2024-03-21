package com.example.demo.src.board.model;

import com.example.demo.src.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetBoardRes {
    private Long id;
    private String content;

    public GetBoardRes(Board board) {
        this.id = board.getId();
        this.content = board.getContent();
    }
}
