package com.example.demo.src.user.model;

import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.demo.common.Constant.SocialLoginType.GOOGLE;
import static com.example.demo.common.Constant.SocialLoginType.KAKAO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUser {

    public String id;
    public String email;
    public String profileNickname;
    public String profileImage;

    public User toEntity() {
        String email = (this.email == null) ? generateDefaultEmail() : this.email;
        return User.builder()
                .email(email) // 이메일 권한 현재 없음 -> 디폴트 값 설정
                .password("NONE")
                .name(this.profileNickname)
                .isOAuth(true)
                .profileImgUrl(this.profileImage)
                .socialLoginType(KAKAO)
                .build();
    }

    private String generateDefaultEmail() {
        LocalDateTime now = LocalDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return formattedNow + "@hanmail.net";
    }
}
