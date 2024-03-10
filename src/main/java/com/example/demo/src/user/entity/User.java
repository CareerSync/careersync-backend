package com.example.demo.src.user.entity;

import com.example.demo.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

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

    @Column(nullable = false)
    private boolean isOAuth;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column
    private String profileImgUrl;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean serviceTerm;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean dataTerm;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean locationTerm;

    @Builder
    public User(Long id, String email, String password, String name, boolean isOAuth, LocalDate birthDate, String profileImgUrl,
                boolean serviceTerm, boolean dataTerm, boolean locationTerm) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isOAuth = isOAuth;
        this.birthDate = birthDate;
        this.profileImgUrl = profileImgUrl;
        this.serviceTerm = serviceTerm;
        this.dataTerm = dataTerm;
        this.locationTerm = locationTerm;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void deleteUser() {
        this.state = State.INACTIVE;
    }

}
