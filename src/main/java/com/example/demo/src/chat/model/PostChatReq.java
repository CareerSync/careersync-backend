package com.example.demo.src.chat.model;

import com.example.demo.src.chat.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostChatReq {

    @NotNull(message = "대화 id는 null 일 수 없습니다.")
    private UUID id;

    @NotBlank(message = "질문은 null 혹은 빈 문자열 일 수 없습니다.")
    private String question;

    public Chat toEntity(PostChatReq req) {
        return Chat.builder()
                .id(req.getId())
                .title(req.getQuestion())
                .build();
    }

}
