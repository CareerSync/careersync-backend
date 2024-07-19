package com.example.demo.common.exceptions.unauthorized.user;

import com.example.demo.common.exceptions.unauthorized.UnauthorizedException;

import static com.example.demo.common.response.BaseResponseStatus.UNAUTHORIZED_USER;

public class UserUnauthorizedException extends UnauthorizedException {
    public UserUnauthorizedException() {
        super(UNAUTHORIZED_USER);
    }
}
