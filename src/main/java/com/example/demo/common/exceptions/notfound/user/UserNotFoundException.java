package com.example.demo.common.exceptions.notfound.user;

import com.example.demo.common.exceptions.notfound.NotFoundException;

import static com.example.demo.common.response.BaseResponseStatus.INVALID_USER;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(INVALID_USER);
    }
}
