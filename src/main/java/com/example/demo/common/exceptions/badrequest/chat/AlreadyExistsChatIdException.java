package com.example.demo.common.exceptions.badrequest.chat;

import com.example.demo.common.exceptions.badrequest.BadRequestException;

import static com.example.demo.common.response.BaseResponseStatus.*;

public class AlreadyExistsChatIdException extends BadRequestException {
    public AlreadyExistsChatIdException() {
        super(CHAT_ID_EXIST);
    }
}
