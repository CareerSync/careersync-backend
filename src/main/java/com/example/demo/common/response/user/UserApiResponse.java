package com.example.demo.common.response.user;

public class UserApiResponse {

    /**
     * 200 SUCCESS
     */
    public static final String USER_INFO_MODIFIED = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:10:27+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "id": "cd8afdff-7e65-487a-ad40-9c165a765b06"
              }
            }
            """;

    /**
     * 201 CREATED
     */
    public static final String USER_CREATED = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T18:51:23+09:00",
              "status": "success",
              "statusCode": 201,
              "message": "유저 생성이 완료되었습니다",
              "data": {
                "id": "dbc23384-a41a-4981-84cb-539762d7a80d"
              }
            }
            """;
    /**
     * 400 BAD_REQUEST
     */
    public static final String USER_ID_EXIST = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T18:52:26+09:00",
              "status": "error",
              "statusCode": 400,
              "message": "The request is invalid.",
              "errors": [
                {
                  "errorCode": "USER_ID_EXIST",
                  "message": "이미 존재하는 유저 아이디입니다."
                }
              ]
            }
            """;

    public static final String USER_NAME_EXIST = """
            {
               "apiVersion": "1.0.0",
               "timestamp": "2024-08-14T18:56:22+09:00",
               "status": "error",
               "statusCode": 400,
               "message": "The request is invalid.",
               "errors": [
                 {
                   "errorCode": "USER_NAME_EXIST",
                   "message": "이미 존재하는 유저 이름입니다."
                 }
               ]
             }
            """;

    public static final String USER_INVALID_REQUEST = """
            {
                "apiVersion": "1.0.0",
                "timestamp": "2024-08-14T18:57:38+09:00",
                "status": "fail",
                "statusCode": 400,
                "message": "INVALID_REQUEST",
                "errors": [
                  {
                    "field": "password",
                    "errorCode": "INVALID_SIZE",
                    "message": "유저 비밀번호는 8자 이상이어야 합니다."
                  },
                  {
                    "field": "userId",
                    "errorCode": "INVALID_SIZE",
                    "message": "유저 아이디는 1자 이상 10자 이내여야 합니다."
                  },
                  {
                    "field": "userId",
                    "errorCode": "REQUIRED_FIELD",
                    "message": "유저 아이디는 null 혹은 빈 문자열 일 수 없습니다."
                  }
                ]
              }
            """;

    public static final String INVALID_USER_TECH_STACKS = """
            {
               "apiVersion": "1.0.0",
               "timestamp": "2024-08-14T19:19:39+09:00",
               "status": "fail",
               "statusCode": 400,
               "message": "INVALID_REQUEST",
               "errors": [
                 {
                   "field": "techStacks",
                   "errorCode": "INVALID_SIZE",
                   "message": "유저 기술스택은 최소 1개 최대 5개이어야 합니다."
                 }
               ]
             }
            """;

    public static final String INVALID_USER_CAREER = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:19:52+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "career",
                  "errorCode": "INVALID_VALUE",
                  "message": "유저 경력 수치는 0 이상이어야 합니다."
                }
              ]
            }
            """;

    public static final String INVALID_USER_EDUCATION = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:20:26+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "education",
                  "errorCode": "REQUIRED_FIELD",
                  "message": "유저 최종학력은 null 혹은 빈 문자열 일 수 없습니다."
                }
              ]
             }
            """;

    /**
     * 404 NOT_FOUND
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
