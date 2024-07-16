package com.example.demo.common.exceptions.unauthorized;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponseStatus;

public class UnAuthorizedException extends BaseException {
    public UnAuthorizedException(BaseResponseStatus status) {
        super(status);
    }

    public UnAuthorizedException(BaseResponseStatus status, String message) {
        super(status, message);
    }
}
