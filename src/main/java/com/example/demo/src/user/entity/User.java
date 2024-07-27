package com.example.demo.src.user.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.test.entity.Comment;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.demo.common.Constant.*;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_USER") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class User extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_user_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "user_name", nullable = false, length = 10, columnDefinition = "nvarchar(10)")
    private String userName;

    @Column(name = "user_id", nullable = false, length = 50, columnDefinition = "nvarchar(50)")
    private String userId;

    @Column(columnDefinition = "text")
    private String password;

    @Column(name = "is_oauth", nullable = false, columnDefinition = "tinyint")
    private Boolean isOAuth;

    @Column(name = "social_login_type", length = 10, columnDefinition = "nvarchar(10)")
    private SocialLoginType socialLoginType;

    // 양방향 매핑
    // @BatchSize(size = 5) // BatchSize 설정 예제
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = ALL)
    List<Chat> chats = new ArrayList<>();

    @Builder
    public User(UUID id, String userName, String userId, String password, Boolean isOAuth, SocialLoginType socialLoginType) {
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.password = password;
        this.isOAuth = isOAuth;
        this.socialLoginType = socialLoginType;
    }

}
