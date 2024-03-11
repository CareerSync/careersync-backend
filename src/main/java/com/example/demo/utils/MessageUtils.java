package com.example.demo.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    private MessageSource messageSource;

    public  MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 언어팩 언어 가져오기
     *
     * @param code 언어팩 코드
     * @return String
     */

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * 언어팩 언어 가져오기
     *
     * @param code 언어팩 코드
     * @param strs 동적 문자
     * @return String
     */

    public String getMessage(String code, String[] strs) {
        return messageSource.getMessage(code, strs, LocaleContextHolder.getLocale());
    }
}
