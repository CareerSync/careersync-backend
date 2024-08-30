package com.example.demo.src.chat.model.ai_server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiServerReq {
    private String user_id;
    private String chat_uuid;
    private String question;
}
