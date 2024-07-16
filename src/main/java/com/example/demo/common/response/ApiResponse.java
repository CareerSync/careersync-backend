package com.example.demo.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.type.Date;
import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"apiVersion", "timestamp", "status", "statusCode", "message", "data"})
public class ApiResponse<T> {

    private final String apiVersion;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private final ZonedDateTime timestamp;
    private final String status;
    private final int statusCode;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private ApiResponse(String apiVersion, BaseResponseStatus baseResponseStatus, T data) {
        this.apiVersion = apiVersion;
        this.timestamp = ZonedDateTime.now();
        this.status = baseResponseStatus.getStatus();
        this.statusCode = baseResponseStatus.getCode();
        this.message = baseResponseStatus.getMessage();
        this.data = data;
    }


    public static <T> ApiResponse<T> success(String apiVersion, BaseResponseStatus baseResponseStatus, T data) {
        return new ApiResponse<T>(apiVersion, baseResponseStatus, data);
    }

    public static <T> ApiResponse<T> success(String apiVersion, T data) {
        return new ApiResponse<T>(apiVersion, BaseResponseStatus.SUCCESS, data);
    }

//    public static <T> ApiResponse<T> fail(ResponseCode responseCode, T data) {
//        return new ApiResponse<T>(new ApiHeader(responseCode.getHttpStatusCode(), responseCode.getMessage()), data, responseCode.getMessage());
//    }

}
