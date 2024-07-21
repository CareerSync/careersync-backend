package com.example.demo.common.exceptions;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.google.protobuf.Api;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.common.response.BaseResponseStatus.INVALID_REQUEST;
import static org.springframework.http.HttpStatus.*;


@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        getErrorCode(error.getCode()),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.fail(BaseResponseStatus.INVALID_REQUEST, errors));
    }

    private String getErrorCode(String code) {
        switch (code) {
            case "NotNull":
            case "NotEmpty":
            case "NotBlank":
                return "REQUIRED_FIELD";
            case "Email":
                return "INVALID_EMAIL";
            case "Size":
                return "INVALID_SIZE";
            default:
                return "INVALID_VALUE";
        }
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException ex) {
        BaseResponseStatus status = ex.getStatus();

        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.fail(status);
        return new ResponseEntity<>(response, HttpStatus.valueOf(status.getCode()));
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
