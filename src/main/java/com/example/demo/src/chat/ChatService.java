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
import com.example.demo.utils.RedisService;
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
    private final RedisService redisService;

    private final RestTemplate restTemplate = new RestTemplate();


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

        // Fetch user
        User user = getUserWithId(userId);

        // 이미 존재하는 대화 uuid면 400 에러 반환
        validateChatExist(chatId);

        // Ensure chat is new or correctly detached if reused
        Chat chat = postChatReq.toEntity(postChatReq);
        chat = chatRepository.save(chat); // Ensures chat is managed in the session
        user.addChats(chat);

        // 유저 기술스택 Redis 에 추가
        //redisService.addUserTechStackToRedis(userId);

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

//        AiServerRes answerFromAiServer = getAnswerFromAiServer(user_id, chat_uuid, questionStr);

        // 예시 데이터 하드코딩
        // 예시 질문 : "백엔드 개발을 공부중인데 대기업에서도 백엔드 개발자를 뽑고있어?"
//        {
//            "answer": "네, 대기업에서도 백엔드 개발자를 채용하고 있습니다. 예를 들어, (주)크래프톤과 (주)노스글로벌에서 백엔드 개발자를 모집 중입니다.",
//                "jobPosts": [],
//            "is_true": false
//        }
        // Manually create AiServerRes object with hardcoded example data
        AiServerRes answerFromAiServer = new AiServerRes();
        answerFromAiServer.setAnswer("네, 대기업에서도 백엔드 개발자를 채용하고 있습니다. 예를 들어, (주)크래프톤과 (주)노스글로벌에서 백엔드 개발자를 모집 중입니다.");
        answerFromAiServer.set_true(false);

        // If you have a list of job posts, populate it here, or set to an empty list
        List<AiServerJobPost> jobPosts = new ArrayList<>();
        // Example: Add job posts if needed
        // AiServerJobPost jobPost1 = new AiServerJobPost("Job Title", "Company Name", ...);
        // jobPosts.add(jobPost1);
        answerFromAiServer.setJobPosts(jobPosts);

        // Handle question response
        return handleQuestionResponse(answerFromAiServer, user, chat, questionStr, true);
    }


    public PostChatRes addAnswerToChat(UUID userId, UUID chatId, PostAfterChatReq postAfterChatReq) throws JsonProcessingException {

        User user = getUserWithId(userId);
        // Fetch existing chat entity
        Chat chat = getChatWithId(chatId);

        String questionStr = postAfterChatReq.getQuestion();

        // 유저 기술스택 Redis 에 추가
        //redisService.addUserTechStackToRedis(userId);

        // Create and add question to chat
        Question question = Question.builder()
                .question_text(questionStr)
                .build();
        // Persist question
        questionRepository.save(question);
        chat.addQuestions(question);

        String user_id = userId.toString();
        String chat_uuid = chatId.toString();

        //AiServerRes answerFromAiServer = getAnswerFromAiServer(user_id, chat_uuid, questionStr);

        // 예시 질문 :

        // 예시 답변
//        {
//            "answer": "다음 채용공고가 당신에게 적합할 것 같습니다:\n\n1. **(주)제이앤씨에이치알**\n   - **위치:** 인천 청라\n   - **경력:** 3~15년\n   - **기술:** Java, Spring\n   - **링크:** [채용공고](https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48895077&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002)\n\n2. **(주)파미컴퍼니**\n   - **위치:** 서울 구로구\n   - **경력:** 3년 이상\n   - **기술:** Java, Spring, DataBase, javascript, HTML5\n   - **링크:** [채용공고](https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48892247&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002)\n\n3. **(주)에스유스카우트**\n   - **위치:** 서울 강남구\n   - **경력:** 8~13년\n   - **기술:** JPA, Spring Boot, MySQL, Git\n   - **링크:** [채용공고](https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48814269&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495)",
//                "jobPosts": [
//            {
//                "title": "JAVA Spring 개발자(인천 청라)",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48895077&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002",
//                    "imgUrl": null,
//                    "endDate": "2024-09-02",
//                    "education": "대졸(4년제) 이상",
//                    "workHistory": "경력 3~15년",
//                    "companyName": "(주)제이앤씨에이치알"
//            },
//            {
//                "title": "Java기반 Front/Backend 시스템 개발 및 운영 - 셀럽중개",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48828617&location=ts&searchType=search&paid_fl=n&search_uuid=6c36fbe5-0f81-4148-8560-c7880ecdc2c5",
//                    "imgUrl": null,
//                    "endDate": "2024-09-17",
//                    "education": "학력무관",
//                    "workHistory": "경력 5~15년",
//                    "companyName": "(주)피플케어코리아"
//            },
//            {
//                "title": "[삼성케어플러스] Java/Spring Back-end 개발자를 모십니다.",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48892247&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002",
//                    "imgUrl": null,
//                    "endDate": "채용시",
//                    "education": "학력무관",
//                    "workHistory": "경력 3년 ↑",
//                    "companyName": "(주)파미컴퍼니"
//            },
//            {
//                "title": "[핀테크 기업] Java 백엔드 개발자(Senior)",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48814269&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495",
//                    "imgUrl": null,
//                    "endDate": "2024-08-25",
//                    "education": "대졸(2,3년제) 이상",
//                    "workHistory": "경력 8~13년",
//                    "companyName": "(주)에스유스카우트"
//            },
//            {
//                "title": "양자기술TF 소프트웨어 파트 백엔드 개발자 채용",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48815679&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495",
//                    "imgUrl": null,
//                    "endDate": "2024-09-13",
//                    "education": "대졸(4년제) 이상",
//                    "workHistory": "경력 5~10년",
//                    "companyName": "에스디티(주)"
//            },
//            {
//                "title": "[헬스케어서비스기업 ] JAVA 시스템 개발 대리 과장급 채용",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48898284&location=ts&searchType=search&paid_fl=n&search_uuid=685189c8-e868-4a5f-8923-e773fd5b0c4b",
//                    "imgUrl": null,
//                    "endDate": "채용시",
//                    "education": "학력무관",
//                    "workHistory": "경력 5~8년",
//                    "companyName": "(주)베스트에치알 (Best HR)"
//            },
//            {
//                "title": "백엔드 개발자 채용[나주지사]",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48812950&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495",
//                    "imgUrl": null,
//                    "endDate": "2024-09-17",
//                    "education": "대졸(4년제) 이상",
//                    "workHistory": "경력 2~10년",
//                    "companyName": "비엠텍시스템(주)"
//            },
//            {
//                "title": "[JAVA SI 서울] HR시스템 개발, PL급, 분석,설계, 고급이상 (1)",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48906743&location=ts&searchType=search&paid_fl=n&search_uuid=928f362e-8c93-4f39-a70c-8e31f50db019",
//                    "imgUrl": null,
//                    "endDate": "2024-09-04",
//                    "education": "대졸(2,3년제) 이상",
//                    "workHistory": "경력 9년 ↑",
//                    "companyName": "(주)제이투이"
//            },
//            {
//                "title": "[자바프로그램개발자] 웹개발 경력직 채용",
//                    "siteUrl": "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48898024&location=ts&searchType=search&paid_fl=n&search_uuid=685189c8-e868-4a5f-8923-e773fd5b0c4b",
//                    "imgUrl": null,
//                    "endDate": "2024-09-26",
//                    "education": "대졸(2,3년제) 이상",
//                    "workHistory": "경력 3년 ↑",
//                    "companyName": "(주)라운드원"
//            }
//  ],
//            "is_true": true
//        }

        // Manually create AiServerRes object with hardcoded example data
        AiServerRes answerFromAiServer = new AiServerRes();
        answerFromAiServer.setAnswer("다음 채용공고가 당신에게 적합할 것 같습니다:\n\n1. **(주)제이앤씨에이치알**\n   - **위치:** 인천 청라\n   - **경력:** 3~15년\n   - **기술:** Java, Spring\n   - **링크:** [채용공고](https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48895077&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002)\n\n2. **(주)파미컴퍼니**\n   - **위치:** 서울 구로구\n   - **경력:** 3년 이상\n   - **기술:** Java, Spring, DataBase, javascript, HTML5\n   - **링크:** [채용공고](https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48892247&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002)\n\n3. **(주)에스유스카우트**\n   - **위치:** 서울 강남구\n   - **경력:** 8~13년\n   - **기술:** JPA, Spring Boot, MySQL, Git\n   - **링크:** [채용공고](https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48814269&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495)");

        answerFromAiServer.set_true(true);

        // Create example job posts
        List<AiServerJobPost> jobPosts = new ArrayList<>();

        // 예시 데이터 추가
        AiServerJobPost jobPost1 = new AiServerJobPost(
                "JAVA Spring 개발자(인천 청라)",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48895077&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002",
                "https://picsum.photos/id/1/200/300",
                "2024-09-02",
                "대졸(4년제) 이상",
                "경력 3~15년",
                "(주)제이앤씨에이치알"
        );
        jobPosts.add(jobPost1);

        AiServerJobPost jobPost2 = new AiServerJobPost(
                "Java기반 Front/Backend 시스템 개발 및 운영 - 셀럽중개",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48828617&location=ts&searchType=search&paid_fl=n&search_uuid=6c36fbe5-0f81-4148-8560-c7880ecdc2c5",
                "https://picsum.photos/id/20/200/300",
                "2024-09-17",
                "학력무관",
                "경력 5~15년",
                "(주)피플케어코리아"
        );
        jobPosts.add(jobPost2);

        AiServerJobPost jobPost3 = new AiServerJobPost(
                "[삼성케어플러스] Java/Spring Back-end 개발자를 모십니다.",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48892247&location=ts&searchType=search&paid_fl=n&search_uuid=b15a5fff-4b3d-4a3b-82e6-0c7f1bd65002",
                "https://picsum.photos/id/48/200/300",
                "채용시",
                "학력무관",
                "경력 3년 ↑",
                "(주)파미컴퍼니"
        );
        jobPosts.add(jobPost3);

        AiServerJobPost jobPost4 = new AiServerJobPost(
                "[핀테크 기업] Java 백엔드 개발자(Senior)",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48814269&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495",
                "https://picsum.photos/id/60/200/300",
                "2024-08-25",
                "대졸(2,3년제) 이상",
                "경력 8~13년",
                "(주)에스유스카우트"
        );
        jobPosts.add(jobPost4);

        AiServerJobPost jobPost5 = new AiServerJobPost(
                "양자기술TF 소프트웨어 파트 백엔드 개발자 채용",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48815679&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495",
                "https://picsum.photos/id/119/200/300",
                "2024-09-13",
                "대졸(4년제) 이상",
                "경력 5~10년",
                "에스디티(주)"
        );
        jobPosts.add(jobPost5);

        AiServerJobPost jobPost6 = new AiServerJobPost(
                "[헬스케어서비스기업 ] JAVA 시스템 개발 대리 과장급 채용",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48898284&location=ts&searchType=search&paid_fl=n&search_uuid=685189c8-e868-4a5f-8923-e773fd5b0c4b",
                "https://picsum.photos/id/180/200/300",
                "채용시",
                "학력무관",
                "경력 5~8년",
                "(주)베스트에치알 (Best HR)"
        );
        jobPosts.add(jobPost6);

        AiServerJobPost jobPost7 = new AiServerJobPost(
                "백엔드 개발자 채용[나주지사]",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48812950&location=ts&searchType=search&paid_fl=n&search_uuid=82624d93-e021-41e0-a5e3-6002fa997495",
                "https://picsum.photos/id/201/200/300",
                "2024-09-17",
                "대졸(4년제) 이상",
                "경력 2~10년",
                "비엠텍시스템(주)"
        );
        jobPosts.add(jobPost7);

        AiServerJobPost jobPost8 = new AiServerJobPost(
                "[JAVA SI 서울] HR시스템 개발, PL급, 분석,설계, 고급이상 (1)",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48906743&location=ts&searchType=search&paid_fl=n&search_uuid=928f362e-8c93-4f39-a70c-8e31f50db019",
                "https://picsum.photos/id/357/200/300",
                "2024-09-04",
                "대졸(2,3년제) 이상",
                "경력 9년 ↑",
                "(주)제이투이"
        );
        jobPosts.add(jobPost8);

        AiServerJobPost jobPost9 = new AiServerJobPost(
                "[자바프로그램개발자] 웹개발 경력직 채용",
                "https://www.saramin.co.kr/zf_user/jobs/relay/view?view_type=search&rec_idx=48898024&location=ts&searchType=search&paid_fl=n&search_uuid=685189c8-e868-4a5f-8923-e773fd5b0c4b",
                "https://picsum.photos/id/367/200/300",
                "2024-09-26",
                "대졸(2,3년제) 이상",
                "경력 3년 ↑",
                "(주)라운드원"
        );
        jobPosts.add(jobPost9);

        answerFromAiServer.setJobPosts(jobPosts);

        // Handle question response
        return handleQuestionResponse(answerFromAiServer, user, chat, questionStr, false);
    }

    private AiServerRes getAnswerFromAiServer(String user_id, String chat_uuid, String question) {

        AiServerReq aiServerReq = new AiServerReq(user_id, chat_uuid, question);
        AiServerRes aiServerRes = null;
        List<JobPost> jobPosts = null;

        try {
            String ai_server_url = "http://20.196.65.98:8080/chatbot/chat";
            aiServerRes = restTemplate.postForEntity(ai_server_url, aiServerReq, AiServerRes.class).getBody();
            assert aiServerRes != null;
            log.info("answer : {}", aiServerRes.getAnswer());
            log.info("is_true : {}", aiServerRes.is_true());

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
        //boolean flag = aiServerRes.is_true(); // true: requires job posts, false: does not
        String answerStr;

        List<AiServerJobPost> jobPostsFromAiServer = aiServerRes.getJobPosts();

        List<JobPost> jobPosts = new ArrayList<>();
        answerStr = aiServerRes.getAnswer();

        // Create and add answer
        Answer answer = Answer.builder()
                    .answer_text(answerStr)
                    .build();

        answerRepository.save(answer);
        chat.addAnswers(answer);

        if (!jobPostsFromAiServer.isEmpty()) {
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
        }



        // Redis에 대화 업데이트
        //redisService.addUserChatToRedis(user.getId());

        // save로 대화 업데이트 해야, 더티체킹하면서 updatedAt 컬럼 갱신됨
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
