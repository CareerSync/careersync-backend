package com.example.demo.src.post.model;

import com.example.demo.src.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPostRes {
    private Long id;
    private String content;

    public GetPostRes(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
    }
}
