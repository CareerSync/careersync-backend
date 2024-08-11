package com.example.demo.common.exceptions;

public enum GlobalErrorCode {
    INTERNAL_SERVER_ERROR("An unexpected error occurred on the server. Please try again later."),
    AUTHENTICATION_ERROR("Authentication failed."),
    AUTHORIZATION_ERROR("You are not authorized to perform this action."),
    INVALID_REQUEST("The request is invalid."),
    RESOURCE_NOT_FOUND("The requested resource was not found."),
    SERVICE_UNAVAILABLE("The service is currently unavailable. Please try again later.");

    private final String message;

    GlobalErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
