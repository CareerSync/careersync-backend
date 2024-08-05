package com.example.demo.common.exceptions.notfound.user;

import com.example.demo.common.exceptions.unauthorized.UnauthorizedException;

import static com.example.demo.common.response.BaseResponseStatus.ALREADY_LOGGED_OUT_USER;

public class AlreadyLoggedOutUserException extends UnauthorizedException {
    public AlreadyLoggedOutUserException() {
        super(ALREADY_LOGGED_OUT_USER);
    }
}
