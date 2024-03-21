package com.example.demo.src.board.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_BOARD_IMAGE")
public class BoardImage {

    @Id
    @Column(name = "boardImgId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "boardId")
    private Board board;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String origFileName;  // 파일 원본명

    @Column(nullable = false, columnDefinition = "TEXT")
    private String filePath;  // 파일 저장 경로

    @Column(nullable = false)
    private Long fileSize;

    @Builder
    public BoardImage(String origFileName, String filePath, Long fileSize){
        this.origFileName = origFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    // Board 정보 저장
    public void setBoard(Board board){
        this.board = board;

        // 게시글에 현재 파일이 존재하지 않는다면
        if(!board.getBoardImgList().contains(this))
            // 파일 추가
            board.getBoardImgList().add(this);
    }

}
