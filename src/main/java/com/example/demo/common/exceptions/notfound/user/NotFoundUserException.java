package com.example.demo.common.exceptions.notfound.user;

import com.example.demo.common.exceptions.notfound.NotFoundException;

import static com.example.demo.common.response.BaseResponseStatus.NOT_FIND_USER;

public class NotFoundUserException extends NotFoundException {
    public NotFoundUserException() {
        super(NOT_FIND_USER);
    }
}
