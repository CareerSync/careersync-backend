package com.example.demo.src.post.model;

import com.example.demo.src.post.entity.Post;
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
public class PostPostReq {
    private Long userId;
    private String content;

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .content(content)
                .build();
    }
}
