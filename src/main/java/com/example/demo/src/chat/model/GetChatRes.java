package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRes {

    private UUID id;
    private String title;
    private List<QAItemDto> list = new ArrayList<>();

    public GetChatRes(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public void addQAItem(QAItemDto item) {
        list.add(item);
    }

}
