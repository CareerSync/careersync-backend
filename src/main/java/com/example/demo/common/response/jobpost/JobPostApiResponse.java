package com.example.demo.common.response.jobpost;

public class JobPostApiResponse {

    /**
     * 200 SUCCESS
     */
    public static final String GET_ALL_JOB_POSTS_SUCCESS = """
            {
               "apiVersion": "1.0.0",
               "timestamp": "2024-08-26T01:12:14+09:00",
               "status": "success",
               "statusCode": 200,
               "message": "요청에 성공하였습니다.",
               "data": [
                 {
                   "id": "8c9e5553-ffb2-414c-acfe-93917680f338",
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
                   "id": "d943b97d-df4f-4b0d-8349-c1d09827311e",
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
}
