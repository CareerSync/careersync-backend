package com.example.demo.common.exceptions.badrequest.user;

import com.example.demo.common.exceptions.badrequest.BadRequestException;

import static com.example.demo.common.response.BaseResponseStatus.USER_ID_EXIST;

public class AlreadyExistsUserIdException extends BadRequestException {
    public AlreadyExistsUserIdException() {
        super(USER_ID_EXIST);
    }
}
