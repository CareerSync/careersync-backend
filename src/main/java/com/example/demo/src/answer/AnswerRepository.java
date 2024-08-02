package com.example.demo.src.answer;

import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
}
