package com.example.demo.src.chat;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.answer.AnswerRepository;
import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.chat.model.PostChatRes;
import com.example.demo.src.jobpost.JobPostRepository;
import com.example.demo.src.jobpost.JobPostTechStackRepository;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.jobpost.entity.JobPostRes;
import com.example.demo.src.jobpost.entity.JobPostTechStack;
import com.example.demo.src.question.QuestionRepository;
import com.example.demo.src.question.entity.Question;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.TechStack;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.SHA256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final JobPostRepository jobPostRepository;
    private final JobPostTechStackRepository jobPostTechStackRepository;

    //POST
    public PostChatRes createFirstChat(UUID id, PostChatReq postChatReq) {

        // 유저 가져오기
        User user = getUserWithId(id);

        // 대화 생성
        Chat chat = Chat.builder()
                .id(postChatReq.getId())
                .title(postChatReq.getQuestion())
                .build();

        user.addChats(chat);

        // 질문 추출
        String questionStr = postChatReq.getQuestion();
        Question question = Question.builder()
                .question_text(questionStr)
                .build();

       chat.addQuestions(question);

        // TODO 질문에 대한 대답을 AI 서버로부터 가져오기
        return handleQuestionResponse(chat, questionStr);
    }

    private User getUserWithId(UUID id) {
        return userRepository.findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
    }

    private PostChatRes handleQuestionResponse(Chat chat, String questionStr) {

        // 1. 채용공고를 묻는 질문이 아니라면 markdown 형식의 answer 답변만 반환
        // 2. 채용공고를 묻는 질문이면 jobPosts 데이터까지 모두 반환

        boolean flag = false;
        String answerStr;

        // fastapi 서버에서 받은 채용공고 결과 리스트
        // TODO AI 서버에서 응답 가져오는 로직 -> 추후 함수로 따로 빼기
        List<Object> exampleJobPostResult = new ArrayList<>();
        exampleJobPostResult.add("ex1");
        exampleJobPostResult.add("ex2");

        List<JobPost> jobPosts = new ArrayList<>();

        if (flag) {
            answerStr = "sample_answer_from_fastapi_server_without_jobposts";

            Answer answer = Answer.builder()
                    .answer_text(answerStr)
                    .build();

            chat.addAnswers(answer);

        } else {
            answerStr = "sample_answer_from_fastapi_server_with_jobposts";

            Answer answer = Answer.builder()
                    .answer_text(answerStr)
                    .build();

            chat.addAnswers(answer);

            // 채용 공고 DB에 저장 & 유저와 연관관계 매핑까지 완료
            jobPosts = exampleJobPostResult.stream()
                    .map(value -> {

                        // 나중에는 value에서 추출
                        String title = "jobPost_title";
                        String career = "신입";
                        String companyName = "jobPost_coname";
                        LocalDateTime endDate = LocalDateTime.parse("2025-01-01T00:00:00");

                        String siteUrl = "http://test.com";
                        String imgUrl = "http://image.com";
                        List<String> techStacks = new ArrayList<>();
                        techStacks.add("python");
                        techStacks.add("java");

                        JobPost jobPost = JobPost.builder()
                                .title(title)
                                .companyName(companyName)
                                .career(career)
                                .endDate(endDate)
                                .siteUrl(siteUrl)
                                .imageUrl(imgUrl)
                                .build();

                        // 채용공고의 기술 스택 저장
                        List<JobPostTechStack> jobPostTechStacks = techStacks.stream()
                                .map(techStackName -> {
                                    JobPostTechStack techStack = JobPostTechStack.builder()
                                            .name(techStackName)
                                            .build();
                                    jobPost.addJobPostTechStacks(techStack);
                                    return techStack;
                                })
                                .toList();

                        jobPostTechStackRepository.saveAll(jobPostTechStacks);

                        answer.addJobPosts(jobPost);
                        return jobPost;
                    })
                    .toList();

            jobPostRepository.saveAll(jobPosts);
        }

        return createFirstChatResponseDto(questionStr, answerStr, jobPosts);
    }

    private PostChatRes createFirstChatResponseDto(String questionStr, String answerStr, List<JobPost> jobPosts) {
        List<JobPostRes> jobPostResList = JobPostRes.fromEntityList(jobPosts);
        return new PostChatRes(questionStr, answerStr, jobPostResList);
    }

}
