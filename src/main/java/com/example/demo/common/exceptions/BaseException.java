package com.example.demo.common.exceptions;

import com.example.demo.common.response.BaseResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private BaseResponseStatus status;
    private String message;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
    public BaseException(BaseResponseStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
