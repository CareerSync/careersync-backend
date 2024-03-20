package com.example.demo.src.board.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;

import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Audited
@Table(name = "TB_BOARD")
public class Board extends BaseEntity {

    @Id
    @Column(name = "boardId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2200)
    private String content;

    @NotAudited
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public void setUser(User user){
        this.user = user;
    }

    @Builder
    public Board(Long id, String content, User user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }

    public Long findUserId() {
        return user.getId();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateState(State state) {
        this.state = state;
    }

    public void deleteFeed() {
        this.state = INACTIVE;
    }

}
