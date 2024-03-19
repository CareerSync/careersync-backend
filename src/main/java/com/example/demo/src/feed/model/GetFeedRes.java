package com.example.demo.src.feed.model;

import com.example.demo.src.feed.entity.Feed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFeedRes {
    private Long id;
    private String content;

    public GetFeedRes(Feed feed) {
        this.id = feed.getId();
        this.content = feed.getContent();
    }
}
