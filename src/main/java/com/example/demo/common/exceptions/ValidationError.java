package com.example.demo.common.exceptions;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String errorCode;
    private String message;
}
