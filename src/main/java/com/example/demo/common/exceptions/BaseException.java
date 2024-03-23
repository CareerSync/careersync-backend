package com.example.demo.common.exceptions;

import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.utils.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BaseException(BaseResponseStatus status, MessageUtils messageUtils) {
        super(messageUtils.getMessage(status.getMessage()));
        this.status = status;
    }
}
