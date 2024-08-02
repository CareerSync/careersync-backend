package com.example.demo.src.question;

import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
