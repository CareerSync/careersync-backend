package com.example.demo.src.report.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.report.model.GetReportUserRes;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import lombok.*;

import javax.persistence.*;

import static com.example.demo.common.entity.BaseEntity.State.INACTIVE;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@Getter
@Entity
@Table(name = "TB_REPORT")
public class Report extends BaseEntity {

    @Id
    @Column(name = "reportId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @Builder
    public Report(Long id, String category, User user, Post post){
        this.id = id;
        this.category = category;
        this.user = user;
        this.post = post;
    }

    public User getReportedUser(Post post) {
        return post.getUser();
    }

    public void updateState(State state) {
        this.state = state;
    }

    public void updateCategory(String category) {
        this.category = category;
    }

    public void deleteReport() {
        this.state = INACTIVE;
    }

}
