package com.example.demo.src.chat.model;

import com.example.demo.src.chat.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostAfterChatReq {

    @NotBlank(message = "질문은 null 혹은 빈 문자열 일 수 없습니다.")
    private String question;
}
