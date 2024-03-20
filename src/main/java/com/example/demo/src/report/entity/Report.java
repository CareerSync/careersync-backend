package com.example.demo.src.report.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.feed.entity.Feed;
import com.example.demo.src.user.entity.User;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;

import static com.example.demo.common.entity.BaseEntity.State.INACTIVE;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Audited
@Table(name = "TB_REPORT")
public class Report extends BaseEntity {

    @Id
    @Column(name = "reportId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String category;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedId")
    private Feed feed;

    @Builder
    public Report(Long id, String category, User user, Feed feed){
        this.id = id;
        this.category = category;
        this.user = user;
        this.feed = feed;
    }

    public User getReportedUser(Feed feed) {
        return feed.getUser();
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
