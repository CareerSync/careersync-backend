package com.example.demo.src.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginReq {

    @NotBlank(message = "유저 아이디는 null 혹은 빈 문자열 일 수 없습니다.")
    private String loginId;

    @NotBlank(message = "유저 비밀번호는 null 혹은 빈 문자열 일 수 없습니다.")
    private String password;
}
