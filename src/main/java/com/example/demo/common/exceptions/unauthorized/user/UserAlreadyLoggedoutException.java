package com.example.demo.common.exceptions.unauthorized.user;

import com.example.demo.common.exceptions.unauthorized.UnauthorizedException;
import com.example.demo.common.response.BaseResponseStatus;

import static com.example.demo.common.response.BaseResponseStatus.ALREADY_LOGGED_OUT_USER;

public class UserAlreadyLoggedoutException extends UnauthorizedException {
    public UserAlreadyLoggedoutException() {
        super(ALREADY_LOGGED_OUT_USER);
    }
}
