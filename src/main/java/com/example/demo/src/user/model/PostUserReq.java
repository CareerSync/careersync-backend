package com.example.demo.src.user.model;

import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {

    private String userName;
    private String userId;
    private String password;
    private Boolean isOAuth;

    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .userId(this.userId)
                .password(this.password)
                .isOAuth(false)
                .build();
    }
}
