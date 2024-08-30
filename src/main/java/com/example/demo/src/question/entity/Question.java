package com.example.demo.src.question.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.chat.entity.Chat;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_QUESTION")
public class Question extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_question_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "question_text", nullable = false, columnDefinition = "nvarchar(1000)")
    private String question_text;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_chat_id")
    private Chat chat;

    @Builder
    public Question(String question_text) {
        this.question_text = question_text;
    }
}
