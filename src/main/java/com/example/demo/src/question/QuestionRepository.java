package com.example.demo.src.question;

import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.question.entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    @Query("SELECT q FROM Question q " +
            "JOIN FETCH q.chat c " +
            "JOIN FETCH c.user u " +
            "WHERE u.id = :userId " +
            "ORDER BY q.createdAt DESC")
    List<Question> findTop5LatestQuestionsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
