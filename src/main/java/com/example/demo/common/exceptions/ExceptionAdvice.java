package com.example.demo.common.exceptions;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.google.protobuf.Api;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;


@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BaseException.class)
    public ApiResponse<BaseResponseStatus> BaseExceptionHandle(BaseException exception) {
        log.warn("BaseException. error message: {}", exception.getMessage());
        return ApiResponse.fail(exception.getStatus(), null);
    }

    @ExceptionHandler(SQLException.class)
    public ApiResponse<BaseResponseStatus> sqlExceptionHandle(SQLException exception) {
        log.warn("SQLException. error message: {}", exception.getMessage());
        return ApiResponse.fail(BaseResponseStatus.SQL_ERROR, null);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<BaseResponseStatus> ExceptionHandle(Exception exception) {
        log.error("Exception has occured. ", exception);
        return ApiResponse.fail(BaseResponseStatus.UNEXPECTED_ERROR, null);
    }
}
