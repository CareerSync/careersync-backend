package com.example.demo.src.user.model;

import com.example.demo.common.Constant;
import com.example.demo.common.Constant.SocialLoginType;
import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {

    private String email;
    private String password;
    private String name;
    private String profileImgUrl;
    private SocialLoginType socialLoginType;

    private boolean isOAuth;
    private boolean serviceTerm;
    private boolean dataTerm;
    private boolean locationTerm;


    private LocalDate birthDate;


    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .isOAuth(this.isOAuth)
                .birthDate(this.birthDate)
                .privacyDate(LocalDate.now().plusYears(1))
                .profileImgUrl(this.profileImgUrl)

                .serviceTerm(this.serviceTerm)
                .dataTerm(this.dataTerm)
                .locationTerm(this.locationTerm)
                .build();
    }
}
