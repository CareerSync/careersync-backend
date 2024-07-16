package com.example.demo.common.exceptions.unauthorized;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponseStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(BaseResponseStatus status) {
        super(status);
    }

    public UnauthorizedException(BaseResponseStatus status, String message) {
        super(status, message);
    }
}
