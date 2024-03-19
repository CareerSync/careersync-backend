package com.example.demo.src.feed.entity;

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
@Table(name = "TB_FEED")
public class Feed extends BaseEntity {

    @Id
    @Column(name = "feedId", nullable = false, updatable = false)
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
    public Feed(Long id, String content, User user) {
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

    public void deleteFeed() {
        this.state = INACTIVE;
    }

}
