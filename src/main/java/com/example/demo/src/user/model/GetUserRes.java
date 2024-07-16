package com.example.demo.src.user.model;


import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRes {
    private UUID id;
    private String userName;
    private String userId;

    private LocalDate privacyDate;

    public GetUserRes(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.userId = user.getUserId();
    }
}
