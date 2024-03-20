package com.example.demo.src.user.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.feed.entity.Feed;
import com.example.demo.src.report.entity.Report;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.common.Constant.*;
import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@JsonAutoDetect(fieldVisibility = ANY)
@Audited
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

    @Column()
    private LocalDate birthDate;

    @Column()
    private LocalDate privacyDate;

    @Column()
    private String profileImgUrl;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean serviceTerm;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean dataTerm;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean locationTerm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountState accountState = AccountState.ACTIVE;

    @Column(length = 10)
    private SocialLoginType socialLoginType;

    // 양방향 매핑
    @NotAudited
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    List<Feed> feedList = new ArrayList<>();

    // 양방향 매핑
    @NotAudited
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    List<Report> reportList = new ArrayList<>();

    public enum AccountState {
        ACTIVE, DORMANT, BLOCKED;
    }

    // 구글 로그인 전용 Builder
    @Builder
    public User(Long id, String email, String password, String name, boolean isOAuth, String profileImgUrl, SocialLoginType socialLoginType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isOAuth = isOAuth;
        this.socialLoginType = socialLoginType;
        this.profileImgUrl = profileImgUrl;
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

    public void updateBirthDate(LocalDate localDate) {
        this.birthDate = localDate;
    }

    public void updatePrivacyTerm(boolean serviceTerm, boolean dataTerm, boolean locationTerm) {
        this.serviceTerm = serviceTerm;
        this.dataTerm = dataTerm;
        this.locationTerm = locationTerm;
        this.privacyDate = LocalDate.now();
    }

    public void deleteUser() {
        this.state = INACTIVE;
    }

    // 연관관계 편의 메서드
    public void addPost(Feed feed) {
        feed.setUser(this);
        feedList.add(feed);
    }

    public void updateState(State state) {
        this.state = state;
    }

}
