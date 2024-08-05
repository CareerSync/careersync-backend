package com.example.demo.src.chat;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.exceptions.badrequest.chat.AlreadyExistsChatIdException;
import com.example.demo.common.exceptions.notfound.chat.NotFoundChatException;
import com.example.demo.common.exceptions.notfound.user.NotFoundUserException;
import com.example.demo.src.answer.AnswerRepository;
import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.chat.model.*;
import com.example.demo.src.jobpost.JobPostRepository;
import com.example.demo.src.jobpost.JobPostTechStackRepository;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.jobpost.entity.JobPostRes;
import com.example.demo.src.jobpost.entity.JobPostTechStack;
import com.example.demo.src.question.QuestionRepository;
import com.example.demo.src.question.entity.Question;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.utils.DateTimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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


    // GET
    public GetChatRes getChats(UUID userId, Pageable pageable) {
        // Fetch User
        User user = getUserWithId(userId);

        // Fetch chats with pagination
        Page<Chat> chatPage = chatRepository.findByUserAndState(user, ACTIVE, pageable);

        // Map each chat to a ChatInfo DTO
        List<ChatInfo> chatInfoList = chatPage
                .stream()
                .map(chat -> {
                    // Calculate the number of recommended job posts for this chat
                    int recJobPostNum = chat.getJobPosts().size();

                    // Return a new ChatInfo object
                    return new ChatInfo(chat.getId(), chat.getTitle(), DateTimeFormatterUtil.LocalDateTimeToString(chat.getUpdatedAt()), recJobPostNum);
                })
                .toList();

        return new GetChatRes(chatInfoList, pageable.getPageNumber(), pageable.getPageSize());
    }

    //POST
    public PostChatRes createChat(UUID userId, UUID chatId, PostChatReq postChatReq) {
        // Fetch user
        User user = getUserWithId(userId);

        // 이미 존재하는 대화 uuid면 400 에러 반환
        validateChatExist(chatId);

        // Ensure chat is new or correctly detached if reused
        Chat chat = postChatReq.toEntity(postChatReq);
        chat = chatRepository.save(chat); // Ensures chat is managed in the session
        user.addChats(chat);

        // First question as the chat title
        String questionStr = postChatReq.getQuestion();

        // Create question entity and associate with chat
        Question question = Question.builder()
                .question_text(questionStr)
                .build();
        // Persist question
        questionRepository.save(question); // Ensures question is managed
        chat.addQuestions(question);



        // Handle question response
        return handleQuestionResponse(user, chat, questionStr, true);
    }


    public PostChatRes addAnswerToChat(UUID userId, UUID chatId, PostAfterChatReq postAfterChatReq) {

        User user = getUserWithId(userId);
        // Fetch existing chat entity
        Chat chat = getChatWithId(chatId);

        String questionStr = postAfterChatReq.getQuestion();

        // Create and add question to chat
        Question question = Question.builder()
                .question_text(questionStr)
                .build();
        // Persist question
        questionRepository.save(question);

        chat.addQuestions(question);

        // Handle question response
        return handleQuestionResponse(user, chat, questionStr, false);
    }

    private PostChatRes handleQuestionResponse(User user, Chat chat, String questionStr, boolean isFirstChat) {
        // Determine if the question requires job posts
        boolean flag = true; // true: requires job posts, false: does not
        String answerStr;

        // Placeholder for job post results from external server
        List<Object> exampleJobPostResult = new ArrayList<>();
        exampleJobPostResult.add("ex1");
        exampleJobPostResult.add("ex2");

        List<JobPost> jobPosts = new ArrayList<>();

        if (!flag) {
            answerStr = "sample_answer_from_fastapi_server_without_jobposts";

            // Create and add answer
            Answer answer = Answer.builder()
                    .answer_text(answerStr)
                    .build();

            // Persist answer
            answerRepository.save(answer);

            chat.addAnswers(answer);
        } else {
            answerStr = "sample_answer_from_fastapi_server_with_jobposts";

            // Create and add answer
            Answer answer = Answer.builder()
                    .answer_text(answerStr)
                    .build();
            answerRepository.save(answer);

            // Process job post results
            jobPosts = exampleJobPostResult.stream()
                    .map(value -> {
                        // Extract information for job post
                        String title = "jobPost_title";
                        String career = "신입";
                        String companyName = "jobPost_coname";
                        LocalDateTime endDate = LocalDateTime.parse("2025-01-01T00:00:00");

                        String siteUrl = "http://test.com";
                        String imgUrl = "http://image.com";
                        List<String> techStacks = new ArrayList<>();
                        techStacks.add("python");
                        techStacks.add("java");

                        // Create job post entity
                        JobPost jobPost = JobPost.builder()
                                .title(title)
                                .companyName(companyName)
                                .career(career)
                                .endDate(endDate)
                                .siteUrl(siteUrl)
                                .imageUrl(imgUrl)
                                .build();

                        // Add tech stacks to job post
                        List<JobPostTechStack> jobPostTechStacks = techStacks.stream()
                                .map(techStackName -> {
                                    JobPostTechStack techStack = JobPostTechStack.builder()
                                            .name(techStackName)
                                            .build();
                                    jobPost.addJobPostTechStacks(techStack);
                                    return techStack;
                                })
                                .toList();

                        // Persist tech stacks
                        jobPostTechStackRepository.saveAll(jobPostTechStacks);

                        // Associate job post with user and answer
                        user.addJobPosts(jobPost);
                        answer.addJobPosts(jobPost);
                        return jobPost;
                    })
                    .toList();

            // Persist job posts and answer
            jobPostRepository.saveAll(jobPosts);
            chat.addAnswers(answer);

        }

        // Return response DTO
        return createChatResponseDto(questionStr, answerStr, jobPosts, isFirstChat);
    }


    private PostChatRes createChatResponseDto(String questionStr, String answerStr, List<JobPost> jobPosts, boolean isFirstChat) {
        List<JobPostRes> jobPostResList = JobPostRes.fromEntityList(jobPosts);

        if (isFirstChat) {
            return new PostChatRes(questionStr, answerStr, jobPostResList);
        } else {
            return new PostAfterChatRes(answerStr, jobPostResList);
        }
    }

    private User getUserWithId(UUID id) {
        return userRepository.findByIdAndState(id, ACTIVE)
                .orElseThrow(NotFoundUserException::new);
    }

    private Chat getChatWithId(UUID id) {
        return chatRepository.findByIdAndState(id, ACTIVE)
                .orElseThrow(NotFoundChatException::new);
    }

    private void validateChatExist(UUID id) {
        Optional<Chat> chatOptional = chatRepository.findByIdAndState(id, ACTIVE);
        if (chatOptional.isPresent()) {
            throw new AlreadyExistsChatIdException();
        }
    }

}
