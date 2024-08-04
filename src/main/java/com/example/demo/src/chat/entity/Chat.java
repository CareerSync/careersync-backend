package com.example.demo.src.chat.entity;

import com.example.demo.common.Constant;
import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.question.entity.Question;
import com.example.demo.src.test.entity.Memo;
import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_CHAT") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Chat extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_chat_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    private UUID id;

    @Column(name = "title", nullable = false, columnDefinition = "text")
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_user_id")
    private User user;

    @OneToMany(mappedBy = "chat", fetch = LAZY, cascade = ALL)
    List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "chat", fetch = LAZY, cascade = ALL)
    List<Answer> answers = new ArrayList<>();

    @Builder
    public Chat(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public void addQuestions(Question question) {
        questions.add(question);
        question.setChat(this);
    }

    public void addAnswers(Answer answer) {
        answers.add(answer);
        answer.setChat(this);
    }

}
