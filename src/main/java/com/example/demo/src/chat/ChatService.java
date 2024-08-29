package com.example.demo.src.chat;

import com.example.demo.common.exceptions.badrequest.chat.AlreadyExistsChatIdException;
import com.example.demo.common.exceptions.notfound.chat.NotFoundChatException;
import com.example.demo.common.exceptions.notfound.user.NotFoundUserException;
import com.example.demo.src.answer.AnswerRepository;
import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.chat.model.*;
import com.example.demo.src.chat.model.ai_server.AiServerJobPost;
import com.example.demo.src.chat.model.ai_server.AiServerReq;
import com.example.demo.src.chat.model.ai_server.AiServerRes;
import com.example.demo.src.jobpost.JobPostRepository;
import com.example.demo.src.jobpost.JobPostTechStackRepository;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.jobpost.model.JobPostRes;
import com.example.demo.src.jobpost.entity.JobPostTechStack;
import com.example.demo.src.question.QuestionRepository;
import com.example.demo.src.question.entity.Question;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.utils.DateTimeFormatterUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.demo.common.entity.BaseEntity.Status.ACTIVE;
import static com.example.demo.common.entity.BaseEntity.Status.DELETED;

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

    private RestTemplate restTemplate = new RestTemplate();
    private final String ai_server_url = "http://20.196.65.98:8080/chatbot/chat";


    // GET
    @Transactional(readOnly = true)
    public GetChatsRes getChats(UUID userId, Pageable pageable) {
        // Fetch User
        User user = getUserWithId(userId);

        // Fetch chats with pagination
        Page<Chat> chatPage = chatRepository.findByUserAndStatus(user, ACTIVE, pageable);

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

        return new GetChatsRes(chatInfoList, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Transactional(readOnly = true)
    public GetChatRes getChat(UUID userId, UUID chatId) throws JsonProcessingException {

        //redisService.addUserChatToRedis(userId, chatId);

        // chat 식별자로 대화 가져오기
        Chat chat = getChatWithId(chatId);
        return getChatResponseDto(chat);
    }

    @Transactional(readOnly = true)
    public List<JobPostRes> getTop3JobPostsFromChatAndUser(UUID chatId, UUID userId) {

        // 존재하는 유저인지 체크
        getUserWithId(userId);

        // 존재하는 채팅인지 체크
        getChatWithId(chatId);

        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        List<JobPost> jobPosts = jobPostRepository.findTop3ByChatAndUserOrderByCreatedAtDesc(chatId, userId, pageable);
        return JobPostRes.fromEntityList(jobPosts);
    }

    //POST
    public PostChatRes createChat(UUID userId, UUID chatId, PostChatReq postChatReq) throws JsonProcessingException {

        // Update Redis
        //redisService.addUserChatToRedis(userId, chatId);

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

        String user_id = userId.toString();
        String chat_uuid = chatId.toString();

        AiServerRes answerFromAiServer = getAnswerFromAiServer(user_id, chat_uuid, questionStr);

        // Handle question response
        return handleQuestionResponse(answerFromAiServer, user, chat, questionStr, true);
    }


    public PostChatRes addAnswerToChat(UUID userId, UUID chatId, PostAfterChatReq postAfterChatReq) throws JsonProcessingException {

        // Update Redis
        //redisService.addUserChatToRedis(userId, chatId);

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

        String user_id = userId.toString();
        String chat_uuid = chatId.toString();

        AiServerRes answerFromAiServer = getAnswerFromAiServer(user_id, chat_uuid, questionStr);

        // Handle question response
        return handleQuestionResponse(answerFromAiServer, user, chat, questionStr, false);
    }

    private AiServerRes getAnswerFromAiServer(String user_id, String chat_uuid, String question) {

        AiServerReq aiServerReq = new AiServerReq(user_id, chat_uuid, question);
        AiServerRes aiServerRes = null;
        List<JobPost> jobPosts = null;

        try {
            aiServerRes = restTemplate.postForEntity("http://20.196.65.98:8080/chatbot/chat", aiServerReq, AiServerRes.class).getBody();

            assert aiServerRes != null;
            log.info("answer : {}", aiServerRes.getAnswer());

            for (AiServerJobPost jobPost : aiServerRes.getJobPosts()) {
                log.info(jobPost.getTitle());
            }

        } catch (HttpServerErrorException e) {
            log.error("HTTP Status Code: " + e.getStatusCode());
            log.error("HTTP Response Body: " + e.getResponseBodyAsString());
            log.error("Error Message: " + e.getMessage());
            // Handle exception or rethrow
        } catch (Exception e) {
            log.error("An unexpected error occurred: " + e.getMessage());
        }

        return aiServerRes;

    }

    private PostChatRes handleQuestionResponse(AiServerRes aiServerRes, User user, Chat chat, String questionStr, boolean isFirstChat) throws JsonProcessingException {
        // Determine if the question requires job posts
        boolean flag = true; // true: requires job posts, false: does not
        String answerStr;

        // Placeholder for job post results from external server
//        List<Object> exampleJobPostResult = new ArrayList<>();
//        exampleJobPostResult.add("ex1");
//        exampleJobPostResult.add("ex2");

        List<AiServerJobPost> jobPostsFromAiServer = aiServerRes.getJobPosts();

        List<JobPost> jobPosts = new ArrayList<>();

        if (!flag) {
            answerStr = aiServerRes.getAnswer();

            // Create and add answer
            Answer answer = Answer.builder()
                    .answer_text(answerStr)
                    .build();

            // Persist answer
            answerRepository.save(answer);

            chat.addAnswers(answer);
        } else {
            answerStr = aiServerRes.getAnswer();

            // Create and add answer
            Answer answer = Answer.builder()
                    .answer_text(answerStr)
                    .build();
            answerRepository.save(answer);

            // Process job post results
            jobPosts = jobPostsFromAiServer.stream()
                    .map(value -> {
                        // Extract information for job post
                        String title = value.getTitle();
                        String career = value.getWorkHistory();
                        String companyName = value.getCompanyName();
                        String education = value.getEducation();
                        String endDate = value.getEndDate();

                        String siteUrl = value.getSiteUrl();
                        String imgUrl = value.getImgUrl();
//                        List<String> techStacks = new ArrayList<>();
//
//                        techStacks.add("python");
//                        techStacks.add("java");

//                        for (String techStack : techStacks) {
//                            user.addTechStacks(new TechStack(techStack));
//                        }

                        // Create job post entity
                        JobPost jobPost = JobPost.builder()
                                .title(title)
                                .companyName(companyName)
                                .career(career)
                                .endDate(endDate)
                                .siteUrl(siteUrl)
                                .imageUrl(imgUrl)
                                .education(education)
                                .build();

                        // Add tech stacks to job post
//                        List<JobPostTechStack> jobPostTechStacks = techStacks.stream()
//                                .map(techStackName -> {
//                                    JobPostTechStack techStack = JobPostTechStack.builder()
//                                            .name(techStackName)
//                                            .build();
//                                    jobPost.addJobPostTechStacks(techStack);
//                                    return techStack;
//                                })
//                                .toList();

                        // Persist tech stacks
                        //jobPostTechStackRepository.saveAll(jobPostTechStacks);

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

        // Update Redis
        // redisService.addUserChatToRedis(user.getId(), chat.getId());
        // redisService.addUserTechStackToRedis(user.getId());

        // save로 대화 업데이트 해야, 더티체킹하면서 udpatedAt 컬럼 갱신됨
        chat.updateChatDate(LocalDateTime.now());

        // Return response DTO
        return createChatResponseDto(questionStr, answerStr, jobPosts, isFirstChat);
    }

    private GetChatRes getChatResponseDto(Chat chat) {

        // Create a new ChatDTO
        GetChatRes chatRes = new GetChatRes(chat.getId(), chat.getTitle());

        // Convert Questions to QAItemDTO
        List<QAItemDto> questionDTOs = chat.getQuestions().stream()
                .map(question -> new QAItemDto(
                        question.getId(),
                        question.getQuestion_text(),
                        DateTimeFormatterUtil.LocalDateTimeToString(question.getCreatedAt()), // Format createdAt
                        "question",
                        new ArrayList<>())) // Empty list for jobPosts in questions
                .sorted(Comparator.comparing(QAItemDto::getCreatedAt).reversed()) // Sort questions by createdAt
                .toList();

        // Convert Answers to QAItemDTO
        List<QAItemDto> answerDTOs = chat.getAnswers().stream()
                .map(answer -> new QAItemDto(
                        answer.getId(),
                        answer.getAnswer_text(),
                        DateTimeFormatterUtil.LocalDateTimeToString(answer.getCreatedAt()), // Format createdAt
                        "answer",
                        JobPostRes.fromEntityList(answer.getJobPosts())))
                .sorted(Comparator.comparing(QAItemDto::getCreatedAt).reversed()) // Sort answers by createdAt
                .toList();

        // Interleave the sorted lists
        List<QAItemDto> interleavedList = new ArrayList<>();

        // Use indices to track positions in both lists
        int questionIndex = 0;
        int answerIndex = 0;

        // Merge lists by comparing timestamps
        while (questionIndex < questionDTOs.size()) {
            QAItemDto answer = answerDTOs.get(answerIndex);
            interleavedList.add(answer);
            answerIndex += 1;

            QAItemDto question = questionDTOs.get(questionIndex);
            interleavedList.add(question);
            questionIndex += 1;
        }

        // Add the interleaved list to chatRes
        chatRes.setList(interleavedList);

        return chatRes;
    }

    private PostChatRes createChatResponseDto(String questionStr, String answerStr, List<JobPost> jobPosts, boolean isFirstChat) {
        List<JobPostRes> jobPostResList = JobPostRes.fromEntityList(jobPosts);

        if (isFirstChat) {
            return new PostChatRes(questionStr, answerStr, jobPostResList);
        } else {
            return new PostAfterChatRes(answerStr, jobPostResList);
        }
    }

    // PATCH
    public PatchChatRes modifyChatTitle(UUID chatId, String title) {
        Chat chat = getChatWithId(chatId);
        chat.setTitle(title);

        return new PatchChatRes(chatId, title);
    }

    // DELETE
    public DeleteChatRes deleteChat(UUID chatId) {
        Chat chat = getChatWithId(chatId);
        chat.updateState(DELETED);

        return new DeleteChatRes(chatId);
    }

    private User getUserWithId(UUID id) {
        return userRepository.findByIdAndStatus(id, ACTIVE)
                .orElseThrow(NotFoundUserException::new);
    }

    private Chat getChatWithId(UUID id) {
        return chatRepository.findByIdAndStatus(id, ACTIVE)
                .orElseThrow(NotFoundChatException::new);
    }

    private void validateChatExist(UUID id) {
        Optional<Chat> chatOptional = chatRepository.findByIdAndStatus(id, ACTIVE);
        if (chatOptional.isPresent()) {
            throw new AlreadyExistsChatIdException();
        }
    }

}
