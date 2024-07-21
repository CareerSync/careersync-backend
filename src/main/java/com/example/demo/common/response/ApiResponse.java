package com.example.demo.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.type.Date;
import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"apiVersion", "timestamp", "status", "statusCode", "message", "data", "errors"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String apiVersion;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private final ZonedDateTime timestamp;
    private final String status;
    private final int statusCode;
    private final String message;
    private T data;
    private Map<String, String> errors;

    private ApiResponse(BaseResponseStatus baseResponseStatus) {
        this.apiVersion = "1.0.0";
        this.timestamp = ZonedDateTime.now();
        this.status = baseResponseStatus.getStatus();
        this.statusCode = baseResponseStatus.getCode();
        this.message = baseResponseStatus.getMessage();
    }

    private ApiResponse(BaseResponseStatus baseResponseStatus, T data) {
        this.apiVersion = "1.0.0";
        this.timestamp = ZonedDateTime.now();
        this.status = baseResponseStatus.getStatus();
        this.statusCode = baseResponseStatus.getCode();
        this.message = baseResponseStatus.getMessage();
        this.data = data;
    }

    private ApiResponse(BaseResponseStatus baseResponseStatus, Map<String, String> errors) {
        this.apiVersion = "1.0.0";
        this.timestamp = ZonedDateTime.now();
        this.status = baseResponseStatus.getStatus();
        this.statusCode = baseResponseStatus.getCode();
        this.message = baseResponseStatus.getMessage();
        this.errors = errors;
    }


    public static <T> ApiResponse<T> success(BaseResponseStatus baseResponseStatus, T data) {
        return new ApiResponse<T>(baseResponseStatus, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(BaseResponseStatus.SUCCESS, data);
    }

    public static <T> ApiResponse<T> fail(BaseResponseStatus baseResponseStatus, Map<String, String> errors) {
        return new ApiResponse<T>(baseResponseStatus, errors);
    }

    public static <T> ApiResponse<T> fail(BaseResponseStatus baseResponseStatus) {
        return new ApiResponse<T>(baseResponseStatus);
    }

//    public static <T> ApiResponse<T> fail(ResponseCode responseCode, T data) {
//        return new ApiResponse<T>(new ApiHeader(responseCode.getHttpStatusCode(), responseCode.getMessage()), data, responseCode.getMessage());
//    }

}
