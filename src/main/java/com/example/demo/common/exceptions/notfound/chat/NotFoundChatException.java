package com.example.demo.common.exceptions.notfound.chat;

import com.example.demo.common.exceptions.notfound.NotFoundException;

import static com.example.demo.common.response.BaseResponseStatus.*;

public class NotFoundChatException extends NotFoundException {
    public NotFoundChatException() {
        super(NOT_FIND_CHAT);
    }
}
