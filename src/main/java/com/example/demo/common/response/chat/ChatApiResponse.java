package com.example.demo.common.response.chat;

public class ChatApiResponse {

    /**
     * 200 SUCCESS
     */
    public static final String GET_ALL_CHATS_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:57:42+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "list": [
                  {
                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    "title": "string",
                    "updatedAt": "2024-08-14T19:56:49",
                    "recJobPostNum": 2
                  }
                ],
                "page": 0,
                "size": 1
              }
            }
            """;

    public static final String GET_CHAT_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T20:00:20+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "title": "string",
                "list": [
                  {
                    "id": "e4cd0075-b921-4387-8634-b6366c7da1f9",
                    "text": "sample_answer_from_fastapi_server_with_jobposts",
                    "createdAt": "2024-08-14T19:56:49",
                    "type": "answer",
                    "jobPosts": [
                      {
                        "id": "666218a6-a525-46fc-9848-63f25e6b4cb0",
                        "title": "jobPost_title",
                        "career": "신입",
                        "companyName": "jobPost_coname",
                        "endDate": "2025-01-01T00:00:00+09:00",
                        "techStacks": [
                          "java",
                          "python"
                        ],
                        "imgUrl": "http://image.com",
                        "siteUrl": "http://test.com"
                      },
                      {
                        "id": "6c3c92cd-2a51-47fe-805c-69db96192ce3",
                        "title": "jobPost_title",
                        "career": "신입",
                        "companyName": "jobPost_coname",
                        "endDate": "2025-01-01T00:00:00+09:00",
                        "techStacks": [
                          "python",
                          "java"
                        ],
                        "imgUrl": "http://image.com",
                        "siteUrl": "http://test.com"
                      }
                    ]
                  },
                  {
                    "id": "55fe64d8-e8ad-47e2-ac33-8d9a227424f9",
                    "text": "string",
                    "createdAt": "2024-08-14T19:56:49",
                    "type": "question",
                    "jobPosts": []
                  }
                ]
              }
            }
            """;

    public static final String GET_CHAT_JOB_POST_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T20:02:01+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": [
                {
                  "id": "6c3c92cd-2a51-47fe-805c-69db96192ce3",
                  "title": "jobPost_title",
                  "career": "신입",
                  "companyName": "jobPost_coname",
                  "endDate": "2025-01-01T00:00:00+09:00",
                  "techStacks": [
                    "python",
                    "java"
                  ],
                  "imgUrl": "http://image.com",
                  "siteUrl": "http://test.com"
                },
                {
                  "id": "666218a6-a525-46fc-9848-63f25e6b4cb0",
                  "title": "jobPost_title",
                  "career": "신입",
                  "companyName": "jobPost_coname",
                  "endDate": "2025-01-01T00:00:00+09:00",
                  "techStacks": [
                    "java",
                    "python"
                  ],
                  "imgUrl": "http://image.com",
                  "siteUrl": "http://test.com"
                }
              ]
            }
            """;

    public static final String CHAT_MODIFY_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T20:10:21+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "title": "string"
              }
            }
            """;

    public static final String CHAT_DELETED_SUCCESS = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T20:15:57+09:00",
              "status": "success",
              "statusCode": 200,
              "message": "요청에 성공하였습니다.",
              "data": {
                "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
              }
            }
            """;

    /**
     * 201 CREATED
     */
    public static final String CHAT_CREATED = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-04T00:28:17+09:00",
              "status": "success",
              "statusCode": 201,
              "message": "대화 생성 및 질문에 대한 답변이 완료되었습니다",
              "data": {
                "title": "string",
                "answer": "sample_answer_from_fastapi_server_without_jobposts",
                "jobPosts": []
              }
            }
            """;

    public static final String CHAT_CREATED_WITH_JOB_POST = """
            {
                      "apiVersion": "1.0.0",
                      "timestamp": "2024-08-04T00:25:15+09:00",
                      "status": "success",
                      "statusCode": 201,
                      "message": "대화 생성 및 질문에 대한 답변이 완료되었습니다",
                      "data": {
                        "title": "string",
                        "answer": "sample_answer_from_fastapi_server_with_jobposts",
                        "jobPosts": [
                          {
                            "id": "fde8ee04-45fd-4e7b-a0b2-b90be9caccf8",
                            "title": "jobPost_title",
                            "career": "신입",
                            "companyName": "jobPost_coname",
                            "endDate": "2025-01-01T00:00:00+09:00",
                            "techStacks": [
                              "python",
                              "java"
                            ],
                            "imgUrl": "http://image.com",
                            "siteUrl": "http://test.com"
                          },
                          {
                            "id": "4425d2ee-02b6-4d26-8442-b9fafa4bc77f",
                            "title": "jobPost_title",
                            "career": "신입",
                            "companyName": "jobPost_coname",
                            "endDate": "2025-01-01T00:00:00+09:00",
                            "techStacks": [
                              "python",
                              "java"
                            ],
                            "imgUrl": "http://image.com",
                            "siteUrl": "http://test.com"
                          }
                        ]
                      }
                    }
            """;

    public static final String CHAT_CREATED_CONTINUE = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-04T01:09:25+09:00",
              "status": "success",
              "statusCode": 201,
              "message": "추가 질문에 대한 답변이 완료되었습니다",
              "data": {
                "title": null,
                "answer": "sample_answer_from_fastapi_server_without_jobposts",
                "jobPosts": []
              }
            }
            """;

    public static final String CHAT_CREATED_CONTINUE_WITH_JOB_POST = """
            {
                                      "apiVersion": "1.0.0",
                                      "timestamp": "2024-08-04T01:12:30+09:00",
                                      "status": "success",
                                      "statusCode": 201,
                                      "message": "추가 질문에 대한 답변이 완료되었습니다",
                                      "data": {
                                        "title": null,
                                        "answer": "sample_answer_from_fastapi_server_with_jobposts",
                                        "jobPosts": [
                                          {
                                            "id": "a6554dce-076f-470e-9af8-446fd33a8dcc",
                                            "title": "jobPost_title",
                                            "career": "신입",
                                            "companyName": "jobPost_coname",
                                            "endDate": "2025-01-01T00:00:00+09:00",
                                            "techStacks": [
                                              "python",
                                              "java"
                                            ],
                                            "imgUrl": "http://image.com",
                                            "siteUrl": "http://test.com"
                                          },
                                          {
                                            "id": "b6e1fed7-428a-4201-ad6b-2094a62d48d8",
                                            "title": "jobPost_title",
                                            "career": "신입",
                                            "companyName": "jobPost_coname",
                                            "endDate": "2025-01-01T00:00:00+09:00",
                                            "techStacks": [
                                              "python",
                                              "java"
                                            ],
                                            "imgUrl": "http://image.com",
                                            "siteUrl": "http://test.com"
                                          }
                                        ]
                                      }
                                    }
            """;

    /**
     * 400 INVALID REQUEST
     */

    public static final String CHAT_QUESTION_EMPTY = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:48:00+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "question",
                  "errorCode": "REQUIRED_FIELD",
                  "message": "질문은 null 혹은 빈 문자열 일 수 없습니다."
                }
              ]
            }
            """;

    public static final String CHAT_ID_EXIST = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:49:22+09:00",
              "status": "error",
              "statusCode": 400,
              "message": "The request is invalid.",
              "errors": [
                {
                  "errorCode": "CHAT_ID_EXIST",
                  "message": "이미 존재하는 대화 식별자입니다."
                }
              ]
            }
            """;

    public static final String WRONG_CHAT_ID_TYPE = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T20:03:16+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "parameter",
                  "errorCode": "INVALID_TYPE",
                  "message": "Invalid type for parameter 'chatId'. Expected type: 'UUID'. Error: Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; nested exception is java.lang.IllegalArgumentException: Invalid UUID string: 123123"
                }
              ]
            }
            """;

    public static final String WRONG_CHAT_TITLE_TYPE = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T20:13:39+09:00",
              "status": "fail",
              "statusCode": 400,
              "message": "INVALID_REQUEST",
              "errors": [
                {
                  "field": "title",
                  "errorCode": "REQUIRED_FIELD",
                  "message": "대화 제목은 null일 수 없습니다."
                }
              ]
            }
            """;

    /**
     * 404 NOT FOUND
     */
    public static final String NOT_FIND_CHAT = """
            {
              "apiVersion": "1.0.0",
              "timestamp": "2024-08-14T19:57:01+09:00",
              "status": "fail",
              "statusCode": 404,
              "message": "일치하는 대화가 없습니다.",
              "errors": [
                {
                  "errorCode": "NOT_FIND_CHAT",
                  "message": "The requested resource was not found."
                }
              ]
            }
            """;
}
