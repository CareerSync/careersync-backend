package com.example.demo.src.post.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@Getter
@Entity
@Table(name = "TB_POST")
public class Post extends BaseEntity {

    @Id
    @Column(name = "postId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public Post(Long id, String content, User user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }

}
