package com.example.demo.common.exceptions.badrequest;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponseStatus;

public class BadRequestException extends BaseException {
    public BadRequestException(BaseResponseStatus status) {
        super(status);
    }

    public BadRequestException(BaseResponseStatus status, String message) {
        super(status, message);
    }
}
