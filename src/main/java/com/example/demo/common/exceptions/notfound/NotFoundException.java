package com.example.demo.common.exceptions.notfound;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponseStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(BaseResponseStatus status) {
        super(status);
    }

    public NotFoundException(BaseResponseStatus status, String message) {
        super(status, message);
    }
}
