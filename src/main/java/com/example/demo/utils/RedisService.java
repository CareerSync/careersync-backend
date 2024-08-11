package com.example.demo.utils;

import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.chat.ChatRepository;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.question.entity.Question;
import com.example.demo.src.user.TechStackRepository;
import com.example.demo.src.user.entity.TechStack;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TechStackRepository techStackRepository;
    private final ChatRepository chatRepository;

    public void addUserTechStackToRedis(UUID key) throws JsonProcessingException {

        // userId로 이미 존재하는 유저 기술 스택 가져오기
        Optional<List<TechStack>> result = techStackRepository.findAllByUserId(key);

        List<String> techStacks = result.orElseGet(ArrayList::new)
                .stream()
                .map(TechStack::getName)
                .collect(Collectors.toList());

        // Convert UUID to string
        String keyString = key.toString();

        // Convert the list to a JSON string
        String jsonTechStacks = new ObjectMapper().writeValueAsString(techStacks);

        // Store the JSON string in Redis
        redisTemplate.opsForValue().set(keyString, jsonTechStacks);

        // Log the operation
        log.info("Tech stacks for user '{}' have been added to Redis: {}", keyString, jsonTechStacks);
    }

    public void addUserChatToRedis(UUID userId, UUID chatId) throws JsonProcessingException {
        // Get the top 5 latest questions and answers for the chat
        Pageable pageable = PageRequest.of(0, 5);

        List<Question> latestQuestions = chatRepository.findTop5LatestQuestionsByChatId(chatId, pageable);
        List<Answer> latestAnswers = chatRepository.findTop5LatestAnswersByChatId(chatId, pageable);

        // Initialize a list to store the combined data
        List<Map<String, String>> chatData = new ArrayList<>();

        // Pair questions with answers
        int size = Math.min(latestQuestions.size(), latestAnswers.size());
        for (int i = 0; i < size; i++) {
            Map<String, String> chatPair = new HashMap<>();
            chatPair.put("question", latestQuestions.get(i).getQuestion_text()); // Adjust according to your method name
            chatPair.put("answer", latestAnswers.get(i).getAnswer_text());       // Adjust according to your method name
            chatData.add(chatPair);
        }
        // Convert the combined list to a JSON string
        String jsonChatData = new ObjectMapper().writeValueAsString(chatData);

        // Create the Redis key using userId and chatId
        String keyString = userId.toString() + ":" + chatId.toString();

        // Store the JSON string in Redis
        redisTemplate.opsForValue().set(keyString, jsonChatData);

        // Log the operation
        log.info("Chat data for user '{}' and chat '{}' have been added to Redis: {}", userId, chatId, jsonChatData);
    }



    public void deleteUserChatAndTechStacks(UUID key) {
        String keyPattern = key.toString() + "*"; // Matches keys that start with the UUID

        // Scan for keys that match the pattern
        Set<String> keysToDelete = redisTemplate.keys(keyPattern);

        if (keysToDelete != null && !keysToDelete.isEmpty()) {
            // Delete the matched keys
            redisTemplate.delete(keysToDelete);
            log.info("Deleted keys: {}", keysToDelete);
        } else {
            log.info("No keys found for pattern '{}'", keyPattern);
        }
    }


}
