package com.example.demo.src.chat.model.ai_server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiServerJobPost {
    private String title;
    private String siteUrl;
    private String imgUrl;
    private String endDate;
    private String education;
    private String workHistory;
    private String companyName;
}
