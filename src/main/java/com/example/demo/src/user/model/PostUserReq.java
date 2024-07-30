package com.example.demo.src.user.model;

import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

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

    @NotNull(message = "유저 기술스택 리스트는 null 일 수 없습니다.")
    @Size(min = 1, message = "유저 기술스택 리스트는 최소한 하나의 항목을 포함해야 합니다.")
    private List<@NotBlank(message = "유저 기술스택은 null 혹은 빈 문자열 일 수 없습니다.") String> techStacks;

    @NotNull(message = "유저 경력은 null 일 수 없습니다.")
    @PositiveOrZero(message = "유저 경력 수치는 0 이상이어야 합니다.")
    private int career;

    @NotBlank(message = "유저 최종학력은 null 혹은 빈 문자열 일 수 없습니다.")
    private String education;

    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .userId(this.userId)
                .password(this.password)
                .isOAuth(false)
                .career(this.career)
                .education(this.education)
                .build();
    }
}
