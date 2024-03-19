package com.example.demo.src.feed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFeedLogRes {
    private Long logNum;
    private String requestType;
    private LocalDateTime logTime;
}
