package com.example.demo.common.response;

import com.example.demo.common.exceptions.GlobalErrorCode;
import com.example.demo.common.exceptions.ValidationError;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.type.Date;
import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private List<ValidationError> errors;

    // Constructors for standard responses
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

    private ApiResponse(BaseResponseStatus baseResponseStatus, List<ValidationError> errors) {
        this.apiVersion = "1.0.0";
        this.timestamp = ZonedDateTime.now();
        this.status = baseResponseStatus.getStatus();
        this.statusCode = baseResponseStatus.getCode();
        this.message = baseResponseStatus.getMessage();
        this.errors = errors;
    }

    // Constructor for custom error details in the form of a Map
    private ApiResponse(Map<String, Object> errorDetails) {
        this.apiVersion = "1.0.0";
        this.timestamp = ZonedDateTime.now();
        this.status = (String) errorDetails.get("status");
        this.statusCode = (int) errorDetails.get("statusCode");
        this.message = (String) errorDetails.get("message");

        if (errorDetails.containsKey("errors")) {
            this.errors = (List<ValidationError>) errorDetails.get("errors");
        }
    }

    // Success response with data
    public static <T> ApiResponse<T> success(BaseResponseStatus baseResponseStatus, T data) {
        return new ApiResponse<>(baseResponseStatus, data);
    }

    // Success response without data
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(BaseResponseStatus.SUCCESS, data);
    }

    // Failure response with a list of validation errors
    public static <T> ApiResponse<T> fail(BaseResponseStatus baseResponseStatus, List<ValidationError> errors) {
        return new ApiResponse<>(baseResponseStatus, errors);
    }

    // Failure response without additional data
    public static <T> ApiResponse<T> fail(BaseResponseStatus baseResponseStatus) {
        return new ApiResponse<>(baseResponseStatus);
    }

    // Failure response with custom error details in the form of a Map
    public static <T> ApiResponse<T> fail(Map<String, Object> errorDetails) {
        return new ApiResponse<>(errorDetails);
    }
}