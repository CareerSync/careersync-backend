package com.example.demo.src.post.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.user.entity.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

import static com.example.demo.common.entity.BaseEntity.State.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@Getter
@Entity
@Audited
@Table(name = "TB_POST")
public class Post extends BaseEntity {

    @Id
    @Column(name = "postId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2200)
    @Audited(withModifiedFlag = true)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public void setUser(User user){
        this.user = user;
    }

    @Builder
    public Post(Long id, String content, User user) {
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

    public void deletePost() {
        this.state = INACTIVE;
    }

}
