package com.example.demo.src.user.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.post.entity.Post;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.test.entity.Comment;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@Getter
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "TB_USER") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class User extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "userId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isOAuth;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDate privacyDate;

    @Column(nullable = false)
    private String profileImgUrl;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean serviceTerm;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean dataTerm;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean locationTerm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountState accountState = AccountState.ACTIVE;

    // 양방향 매핑
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Post> postList = new ArrayList<>();

    // 양방향 매핑
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Report> reportList = new ArrayList<>();

    public enum AccountState {
        ACTIVE, DORMANT, BLOCKED;
    }

    @Builder
    public User(Long id, String email, String password, String name, boolean isOAuth, LocalDate birthDate, LocalDate privacyDate, String profileImgUrl,
                boolean serviceTerm, boolean dataTerm, boolean locationTerm) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isOAuth = isOAuth;
        this.birthDate = birthDate;
        this.privacyDate = privacyDate;
        this.profileImgUrl = profileImgUrl;
        this.serviceTerm = serviceTerm;
        this.dataTerm = dataTerm;
        this.locationTerm = locationTerm;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePrivacyDate(LocalDate privacyDate) {
        this.privacyDate = privacyDate;
    }

    // 관리자가 신고당한 유저의 계정 정지
    public void updateAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public void deleteUser() {
        this.state = State.INACTIVE;
    }

    // 연관관계 편의 메서드
    public void addPost(Post post) {
        post.setUser(this);
        postList.add(post);
    }

}
