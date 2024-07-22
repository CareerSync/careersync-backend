package com.example.demo.src.user.model;

import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {

    @NotBlank(message = "유저 이름은 null 혹은 빈 문자열 일 수 없습니다.")
    @Size(min = 1, max = 10, message = "유저 이름은 1자 이상 10자 이내여야 합니다.")
    private String userName;

    @NotBlank(message = "유저 아이디는 null 혹은 빈 문자열 일 수 없습니다.")
    @Size(min = 1, max = 10, message = "유저 아이디는 1자 이상 10자 이내여야 합니다.")
    private String userId;

    @NotBlank(message = "유저 비밀번호는 null 혹은 빈 문자열 일 수 없습니다.")
    @Size(min = 8, message = "유저 비밀번호는 8자 이상이어야 합니다.")
    private String password;

    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .userId(this.userId)
                .password(this.password)
                .isOAuth(false)
                .build();
    }
}
