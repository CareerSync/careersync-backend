package com.example.demo.src.jobpost;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.chat.ChatApiResponse;
import com.example.demo.common.response.common.CommonApiResponse;
import com.example.demo.common.response.jobpost.JobPostApiResponse;
import com.example.demo.src.chat.model.GetChatsRes;
import com.example.demo.src.jobpost.model.GetJobPostRes;
import com.example.demo.src.jobpost.model.JobPostRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.example.demo.common.response.ApiResponse.success;
import static com.example.demo.common.response.BaseResponseStatus.SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Tag(name = "jobPost 도메인", description = "채용공고 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/jobposts")
public class JobPostController {

    private final JobPostService jobPostService;

    /**
     * 채용공고 전체 조회 API
     * [GET] /v1/jobposts
     * @return ResponseEntity<ApiResponse<<JobPostRes>>>
     */
    @Operation(summary = "채용공고 전체 조회 API", description = """
            지금까지 생성된 채용공고 내역을 모두 조회한다.  
            
            채용공고가 수정된 날짜 기준으로 내림차순으로 정렬해서 가져온다.  
            
            서버에서 마지막으로 수정된 날짜 기준으로 정렬되도록 설정하였기에, `/v1/jobposts?page=0&size=1` 와 같이, page와 size만 query string으로 지정해주면 된다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = """
                            채용공고 전체 조회 성공
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(value = JobPostApiResponse.GET_ALL_JOB_POSTS_SUCCESS)

                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "로그인 된 사용자가 아닐 경우 에러 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.AUTHENTICATION_ERROR)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = CommonApiResponse.INTERNAL_SERVER_ERROR)
                    )
            )
    })
    @ResponseBody
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<GetJobPostRes>>> getJobPosts (@PageableDefault(size = 12, sort = "updatedAt",  direction = Sort.Direction.DESC) Pageable pageable) {

        List<GetJobPostRes> jobPosts = jobPostService.getJobPosts(pageable);
        return ResponseEntity.status(OK).body(success(SUCCESS, jobPosts));

    }

}
