package com.example.demo.common.exceptions;

import com.example.demo.common.exceptions.notfound.chat.NotFoundChatException;
import com.example.demo.common.exceptions.notfound.user.NotFoundUserException;
import com.example.demo.common.exceptions.unauthorized.user.UnauthorizedUserException;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.demo.common.response.BaseResponseStatus.*;
import static org.springframework.http.HttpStatus.*;


@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 400 BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        getErrorCode(error.getCode()),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.fail(INVALID_REQUEST, errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<BaseResponseStatus>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException. error message: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.fail(INVALID_REQUEST, null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException. error message: {}", ex.getMessage());

        String errorMessage = String.format(
                "Invalid type for parameter '%s'. Expected type: '%s'. Error: %s",
                ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName(), ex.getMessage());

        ApiResponse<List<ValidationError>> response = ApiResponse.fail(INVALID_REQUEST, List.of(new ValidationError(
                "parameter", // or another relevant field
                "INVALID_TYPE",
                errorMessage)));

        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    /**
     * 401 UNAUTHORIZED
     */
    @ExceptionHandler(UnauthorizedUserException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleUnauthorizedUserException(UnauthorizedUserException ex) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());

        // Error details
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorCode", GlobalErrorCode.AUTHENTICATION_ERROR.name());
        errorDetail.put("message", GlobalErrorCode.AUTHENTICATION_ERROR.getMessage());

        // Response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "error");
        responseBody.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        responseBody.put("message", UNAUTHORIZED_USER.getMessage());
        responseBody.put("errors", List.of(errorDetail));

        // Return response entity
        return ResponseEntity.status(UNAUTHORIZED).body(ApiResponse.fail(responseBody));
    }

    /**
     * 404 NOT_FOUND
     */
    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNotFoundUserException(NotFoundUserException ex) {
        log.warn("User not found: {}", ex.getMessage());

        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorCode", NOT_FIND_USER.name());
        errorDetail.put("message", GlobalErrorCode.RESOURCE_NOT_FOUND.getMessage());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", BaseResponseStatus.NOT_FIND_USER.getStatus());
        responseBody.put("statusCode", BaseResponseStatus.NOT_FIND_USER.getCode());
        responseBody.put("message", BaseResponseStatus.NOT_FIND_USER.getMessage());
        responseBody.put("errors", List.of(errorDetail));

        return ResponseEntity.status(NOT_FOUND).body(ApiResponse.fail(responseBody));
    }

    @ExceptionHandler(NotFoundChatException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNotFoundChatException(NotFoundChatException ex) {
        log.warn("Chat not found: {}", ex.getMessage());

        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorCode", NOT_FIND_CHAT.name());
        errorDetail.put("message", GlobalErrorCode.RESOURCE_NOT_FOUND.getMessage());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", BaseResponseStatus.NOT_FIND_CHAT.getStatus());
        responseBody.put("statusCode", BaseResponseStatus.NOT_FIND_CHAT.getCode());
        responseBody.put("message", BaseResponseStatus.NOT_FIND_CHAT.getMessage());
        responseBody.put("errors", List.of(errorDetail));

        return ResponseEntity.status(NOT_FOUND).body(ApiResponse.fail(responseBody));
    }

    /**
     * 500 INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Map<String, Object>>> ExceptionHandle(Exception exception) {
        log.error("Exception has occurred: ", exception);

        GlobalErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("errorCode", errorCode.name());
        errorDetail.put("message", errorCode.getMessage());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "error");
        responseBody.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseBody.put("message", UNEXPECTED_ERROR.getMessage());
        responseBody.put("errors", List.of(errorDetail));

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ApiResponse.fail(responseBody));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException ex) {
        BaseResponseStatus status = ex.getStatus();

        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.fail(status);
        return ResponseEntity.status(status.getCode()).body(response);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<BaseResponseStatus>> sqlExceptionHandle(SQLException exception) {
        log.warn("SQLException. error message: {}", exception.getMessage());
        return ResponseEntity.status(SQL_ERROR.getCode()).body(ApiResponse.fail(SQL_ERROR, null));
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
}
