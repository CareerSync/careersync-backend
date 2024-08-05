package com.example.demo.common.exceptions.badrequest.user;

import com.example.demo.common.exceptions.badrequest.BadRequestException;

import static com.example.demo.common.response.BaseResponseStatus.*;

public class AlreadyExistsUserNameException extends BadRequestException {
    public AlreadyExistsUserNameException() {
        super(USER_NAME_EXIST);
    }
}
