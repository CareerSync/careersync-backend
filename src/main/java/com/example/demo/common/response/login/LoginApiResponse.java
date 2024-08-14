package com.example.demo.common.response.login;

public class LoginApiResponse {

    /**
     * 200 SUCCESS
     */
    public static final String LOGIN_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:33:26+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "id": "ee0f379c-6ec2-40c2-a8a1-c6229dcf59e3",
                "userId": "string",
                "userName": "string"
              }
            }
            """;

    public static final String CHECK_LOGIN_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:35:53+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "id": "ee0f379c-6ec2-40c2-a8a1-c6229dcf59e3",
                "userName": "string"
              }
            }
            """;

    public static final String LOGOUT_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:37:31+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다."
            }
            """;

    public static final String SOCIAL_LOGIN_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:38:53+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "id": "92ee5ea7-7a2e-44d9-bbbc-3bd1a16598b0",
                "accessToken": "ya29.a0AcM612yA5OkfQZxp3uVkU8T8SMisl0Cs4Tu-SvoHuiERFaFexry7KPcP1yyXegL9CC6RtMop9f7p1PSIVi0nkKh-ooHXiqIDewDB0gbiL9DZ9wDW9d3kJkaQmbEQBKzA_kLCkm9YgOei5-Z_bRC1SE7ZtGZo7VMNTAj4aCgYKAdsSARASFQHGX2MiAmTtsKjWu2rhKU7orfgoZw0171",
                "tokenType": "Bearer"
              }
            }
            """;

    /**
     * 400 INVALID REQUEST
     */

    public static final String LOGIN_ID_EMPTY = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:32:09+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "userId",
                  "errorCode": "REQUIRED_FIELD",
                  "message": "유저 아이디는 null 혹은 빈 문자열 일 수 없습니다."
                },
                {
                  "field": "userId",
                  "errorCode": "INVALID_SIZE",
                  "message": "유저 아이디는 1자 이상 10자 이내여야 합니다."
                }
              ]
            }
            """;

    public static final String LOGIN_PW_EMPTY = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:32:25+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "password",
                  "errorCode": "REQUIRED_FIELD",
                  "message": "유저 비밀번호는 null 혹은 빈 문자열 일 수 없습니다."
                },
                {
                  "field": "password",
                  "errorCode": "INVALID_SIZE",
                  "message": "유저 비밀번호는 8자 이상이어야 합니다."
                }
              ]
            }
            """;

    public static final String LOGIN_PW_TOO_SHORT = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:32:43+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "password",
                  "errorCode": "INVALID_SIZE",
                  "message": "유저 비밀번호는 8자 이상이어야 합니다."
                }
              ]
            }
            """;

    /**
     * 404 NOT FOUND
     */
    public static final String NOT_FIND_USER = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:27:53+09:00",
              "status": "fail",
              "statusCode": 404,
              "message": "일치하는 유저가 없습니다.",
              "errors": [
                {
                  "errorCode": "NOT_FIND_USER",
                  "message": "The requested resource was not found."
                }
              ]
            }
            """;

}
