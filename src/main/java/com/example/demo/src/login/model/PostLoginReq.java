package com.example.demo.src.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginReq {

    @NotBlank(message = "유저 아이디는 null 혹은 빈 문자열 일 수 없습니다.")
    @Size(min = 1, max = 10, message = "유저 아이디는 1자 이상 10자 이내여야 합니다.")
    private String userId;

    @NotBlank(message = "유저 비밀번호는 null 혹은 빈 문자열 일 수 없습니다.")
    @Size(min = 8, message = "유저 비밀번호는 8자 이상이어야 합니다.")
    private String password;
}
