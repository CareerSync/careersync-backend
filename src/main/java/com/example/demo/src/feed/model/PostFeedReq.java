package com.example.demo.src.feed.model;

import com.example.demo.src.feed.entity.Feed;
import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFeedReq {
    private Long userId;
    private String content;

    public Feed toEntity(User user) {
        return Feed.builder()
                .user(user)
                .content(content)
                .build();
    }
}
