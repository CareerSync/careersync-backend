package com.example.demo.src.answer.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.question.entity.Question;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_ANSWER")
public class Answer extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_answer_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "answer_text", nullable = false, columnDefinition = "text")
    private String answer_text;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_chat_id")
    private Chat chat;

    @OneToMany(mappedBy = "answer", fetch = LAZY, cascade = ALL)
    List<JobPost> jobPosts = new ArrayList<>();

    @Builder
    public Answer(String answer_text) {
        this.answer_text = answer_text;
    }

    public void addJobPosts(JobPost jobPost) {
        jobPosts.add(jobPost);
        jobPost.setAnswer(this);
    }
}
