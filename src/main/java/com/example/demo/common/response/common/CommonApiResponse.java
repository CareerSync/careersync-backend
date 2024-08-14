package com.example.demo.common.response.common;

public class CommonApiResponse {

    /**
     * 401 UNAUTHORIZED
     */
    public static final String AUTHENTICATION_ERROR = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:06:58+09:00",
              "status": "error",
              "statusCode": 401,
              "message": "로그인 된 사용자가 아닙니다.",
              "errors": [
                {
                  "errorCode": "AUTHENTICATION_ERROR",
                  "message": "Authentication failed."
                }
              ]
            }
            """;

    /**
     * 500 INTERNAL_SERVER_ERROR
     */
    public static final String INTERNAL_SERVER_ERROR = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T18:59:10+09:00",
              "status": "error",
              "statusCode": 500,
              "message": "예상치 못한 에러가 발생했습니다.",
              "errors": [
                {
                  "errorCode": "INTERNAL_SERVER_ERROR",
                  "message": "An unexpected error occurred on the server. Please try again later."
                }
              ]
            }
            """;
}
