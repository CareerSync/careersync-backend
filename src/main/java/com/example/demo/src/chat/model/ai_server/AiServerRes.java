package com.example.demo.src.chat.model.ai_server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiServerRes {
    private String answer;
    private List<AiServerJobPost> jobPosts;
    private boolean is_true;
}
