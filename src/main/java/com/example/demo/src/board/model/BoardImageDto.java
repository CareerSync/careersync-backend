package com.example.demo.src.board.model;

import com.example.demo.src.board.entity.BoardImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class BoardImageDto {
    private String origFileName;  // 파일 원본명
    private String filePath;  // 파일 저장 경로
    private Long fileSize;

    @Builder
    public BoardImageDto(String origFileName, String filePath, Long fileSize) {
        this.origFileName = origFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
}
